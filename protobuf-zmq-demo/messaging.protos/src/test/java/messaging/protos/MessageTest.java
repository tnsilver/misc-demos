package messaging.protos;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.Timestamps;

import messaging.protos.AddressProtos.Address;
import messaging.protos.AddressProtos.Address.AddressType;
import messaging.protos.CanadianProvinceEnumProtos.CanadianProvinceEnum.Province;
import messaging.protos.ContactProtos.Contact;
import messaging.protos.ContactProtos.Contact.Phone;
import messaging.protos.ContactProtos.Contact.Phone.PhoneType;
import messaging.protos.MessageProtos.Message;
import messaging.protos.PersonProtos.Person;
import messaging.protos.USStateEnumProtos.USStateEnum.State;

class MessageTest {

	private static final Logger logger = LoggerFactory.getLogger(MessageTest.class);
	private Message message;

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
		message = Message.newBuilder().build();
	}

	@Test
	@DisplayName("test default build")
	void testDefaultBuild() throws Exception {
		assertNotNull(message);
		logger.debug("Message: {}", JsonFormat.printer().print(message));
	}

	@Test
	@DisplayName("test descriptor")
	void testDescriptor() throws Exception {
		Descriptor descriptor = Message.getDescriptor();
		List<String> fieldNames = descriptor.getFields().stream().map(f -> {
			System.out.printf("name: %d) %s (%s)%n", f.getNumber(), f.getName(), f.getJavaType());
			return f.getName();
		}).collect(Collectors.toList());
		assertAll(
				() -> assertTrue(fieldNames.contains("person")),
					() -> assertTrue(fieldNames.contains("contact")),
					() -> assertTrue(fieldNames.contains("address")));
	}

	@ParameterizedTest(name = "test message with contact")
	@CsvSource("1,Tomer,S,Silverman,tnsilver@gmail.com,052-1234567,MOBILE,true")
	void testMessageWithContact(Long id, String firstName, String middleInitial, String lastName, String email,
			String phoneNumber, String phoneType, boolean valid) throws Exception {
		Contact contact = Contact.newBuilder()
				.setId(id)
				.setFirstName(firstName)
				.setMiddleInitial(middleInitial)
				.setLastName(lastName)
				.setEmail(email)
				.addPhone(
						Phone.newBuilder()
								.setPhoneNumber(phoneNumber)
								.setPhoneType(PhoneType.valueOf(phoneType))
								.build())
				.setValid(valid)
				.build();
		message = Message.newBuilder().setContact(contact).build();
		Message actual = Message.parseFrom(message.toByteArray());
		assertNotNull(actual);
		assertNotNull(actual.getContentCase());
		assertNotEquals(Message.ContentCase.CONTENT_NOT_SET, actual.getContentCase());
		switch (actual.getContentCase()) {
			case ADDRESS:
				fail("Address is the wrong type of message!");
				break;
			case PERSON:
				fail("Person is the wrong type of message!");
				break;
			case CONTACT:
				assertEquals(contact, actual.getContact());
				break;
			default:
				fail("message does not contain a contact");
		}
		logger.debug("Message: {}", JsonFormat.printer().print(message));
	}

	@ParameterizedTest(name = "test message with person")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3,1,Boston City Hall,1 City Hall Square #500,Boston,MA,02201,United States,BUSINESS")
	void testMessageWithPerson(Long id, String guid, String firstName, String middleInitial, String lastName,
			String dateOfBirth, String maritalStatus, Integer numOfChildren, Long addressId, String addressLine1,
			String addressLine2, String city, String state, String zip, String country, String addressType)
			throws Exception {
		Address address = Address.newBuilder()
				.setId(addressId)
				.setAddressLine1(addressLine1)
				.setAddressLine2(addressLine2)
				.setCity(city)
				.setState(State.valueOf(state))
				.setCountry(country)
				.setZip(zip)
				.setAddressType(AddressType.valueOf(addressType))
				.build();
		Person person = Person.newBuilder()
				.setId(id)
				.setGuid(guid)
				.setFirstName(firstName)
				.setMiddleInitial(middleInitial)
				.setDateOfBirth(Timestamps.parse(dateOfBirth))
				.setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus))
				.setNumOfChildren(numOfChildren)
				.addAddress(address)
				.build();
		message = Message.newBuilder().setPerson(person).build();
		Message actual = Message.parseFrom(message.toByteArray());
		assertNotNull(actual);
		assertNotNull(actual.getContentCase());
		assertNotEquals(Message.ContentCase.CONTENT_NOT_SET, actual.getContentCase());
		switch (actual.getContentCase()) {
			case ADDRESS:
				fail("Address is the wrong type of message!");
				break;
			case PERSON:
				assertEquals(person, actual.getPerson());
				break;
			case CONTACT:
				fail("Contact is the wrong type of message!");
				break;
			default:
				fail("message does not contain a person");
		}
		logger.debug("Message: {}", JsonFormat.printer().print(message));
	}

	@ParameterizedTest(name = "test message with address")
	@CsvSource("3,Montreal City Hall,275 Notre-Dame St.,Montreal,QC,H2Y 1C6,Canada,true")
	void testMessageWithAddress(Long id, String addressLine1, String addressLine2, String city, String state,
			String zip, String country, Boolean primary) throws Exception {
		Address address = Address.newBuilder()
				.setId(id)
				.setAddressLine1(addressLine1)
				.setAddressLine2(String.valueOf(addressLine2.toCharArray()))
				.setCity(city)
				.setProvince(Province.valueOf(state))
				.setCountry(country)
				.setZip(zip)
				.setAddressType(AddressType.BUSINESS)
				.build();
		message = Message.newBuilder().setAddress(address).build();
		Message actual = Message.parseFrom(message.toByteArray());
		assertNotNull(actual);
		assertNotNull(actual.getContentCase());
		assertNotEquals(Message.ContentCase.CONTENT_NOT_SET, actual.getContentCase());
		switch (actual.getContentCase()) {
			case ADDRESS:
				assertEquals(address, actual.getAddress());
				break;
			case PERSON:
				fail("Person is the wrong type of message!");
				break;
			case CONTACT:
				fail("Contact is the wrong type of message!");
				break;
			default:
				fail("message does not contain a address");
		}
		logger.debug("Message: {}", JsonFormat.printer().print(message));
	}

	@Test
	@DisplayName("test empty message")
	void testEmptyMessage() throws Exception {
		message = Message.newBuilder().build();
		Message actual = Message.parseFrom(message.toByteArray());
		assertNotNull(actual);
		assertNotNull(actual.getContentCase());
		assertEquals(Message.ContentCase.CONTENT_NOT_SET, actual.getContentCase());
		logger.debug("Message: {}", JsonFormat.printer().print(message));
	}

	@ParameterizedTest(name = "test any with contact")
	@CsvSource("1,Tomer,S,Silverman,tnsilver@gmail.com,052-1234567,MOBILE,true")
	void testAnyWithContact(Long id, String firstName, String middleInitial, String lastName, String email,
			String phoneNumber, String phoneType, boolean valid) throws Exception {
		Contact expected = Contact.newBuilder()
				.setId(id)
				.setFirstName(firstName)
				.setMiddleInitial(middleInitial)
				.setLastName(lastName)
				.setEmail(email)
				.addPhone(
						Phone.newBuilder()
								.setPhoneNumber(phoneNumber)
								.setPhoneType(PhoneType.valueOf(phoneType))
								.build())
				.setValid(valid)
				.build();
		Any any = Any.parseFrom(Any.pack(expected).toByteArray());
		if (!any.is(Contact.class)) {
			fail("message is not a Contact");
		}
		Contact actual = any.unpack(Contact.class);
		assertNotNull(actual);
		assertEquals(expected, actual);
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test any with person")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3,1,Boston City Hall,1 City Hall Square #500,Boston,MA,02201,United States,BUSINESS")
	void testAnyWithPerson(Long id, String guid, String firstName, String middleInitial, String lastName,
			String dateOfBirth, String maritalStatus, Integer numOfChildren, Long addressId, String addressLine1,
			String addressLine2, String city, String state, String zip, String country, String addressType)
			throws Exception {
		Address address = Address.newBuilder()
				.setId(addressId)
				.setAddressLine1(addressLine1)
				.setAddressLine2(addressLine2)
				.setCity(city)
				.setState(State.valueOf(state))
				.setCountry(country)
				.setZip(zip)
				.setAddressType(AddressType.valueOf(addressType))
				.build();
		Person expected = Person.newBuilder()
				.setId(id)
				.setGuid(guid)
				.setFirstName(firstName)
				.setMiddleInitial(middleInitial)
				.setDateOfBirth(Timestamps.parse(dateOfBirth))
				.setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus))
				.setNumOfChildren(numOfChildren)
				.addAddress(address)
				.build();
		Any any = Any.parseFrom(Any.pack(expected).toByteArray());
		if (!any.is(Person.class)) {
			fail("message is not a Person");
		}
		Person actual = any.unpack(Person.class);
		assertNotNull(actual);
		assertEquals(expected, actual);
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test any with address")
	@CsvSource("3,Montreal City Hall,275 Notre-Dame St.,Montreal,QC,H2Y 1C6,Canada,true")
	void testAnyWithAddress(Long id, String addressLine1, String addressLine2, String city, String state, String zip,
			String country, Boolean primary) throws Exception {
		Address expected = Address.newBuilder()
				.setId(id)
				.setAddressLine1(addressLine1)
				.setAddressLine2(String.valueOf(addressLine2.toCharArray()))
				.setCity(city)
				.setProvince(Province.valueOf(state))
				.setCountry(country)
				.setZip(zip)
				.setAddressType(AddressType.BUSINESS)
				.build();
		Any any = Any.parseFrom(Any.pack(expected).toByteArray());
		if (!any.is(Address.class)) {
			fail("message is not a Address");
		}
		Address actual = any.unpack(Address.class);
		assertNotNull(actual);
		assertEquals(expected, actual);
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

}
