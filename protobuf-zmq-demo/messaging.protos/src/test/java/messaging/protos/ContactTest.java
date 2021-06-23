package messaging.protos;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.util.JsonFormat;

import messaging.protos.ContactProtos.Contact;
import messaging.protos.ContactProtos.Contact.Phone;
import messaging.protos.ContactProtos.Contact.Phone.PhoneType;

class ContactTest {

	private static final Logger logger = LoggerFactory.getLogger(ContactTest.class);
	private Contact contact;

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
		contact = Contact.newBuilder().build();
	}

	@Test
	@DisplayName("test default build")
	void testDefaultBuild() throws Exception {
		assertNotNull(contact);
		logger.debug("Message: {}", JsonFormat.printer().print(contact));
	}

	@Test
	@DisplayName("test descriptor")
	void testDescriptor() throws Exception {
		Descriptor descriptor = Contact.getDescriptor();
		List<String> fieldNames = descriptor.getFields().stream().map(f -> {
			System.out.printf("name: %d) %s (%s)%n", f.getNumber(), f.getName(), f.getJavaType());
			return f.getName();
		}).collect(Collectors.toList());
		assertAll(() -> assertTrue(fieldNames.contains("id")),
				() -> assertTrue(fieldNames.contains("first_name")),
				() -> assertTrue(fieldNames.contains("middle_initial")),
				() -> assertTrue(fieldNames.contains("last_name")),
				() -> assertTrue(fieldNames.contains("email")),
				() -> assertTrue(fieldNames.contains("phone")),
				() -> assertTrue(fieldNames.contains("valid")));
	}

	@ParameterizedTest(name = "test build contact")
	@CsvSource({ "1,Tomer,S,Silverman,tnsilver@gmail.com,052-1234567,MOBILE,true",
			"2,Marge,D,Simpson,marge@simpsons.com,001-12-1234567,HOME,false" })
	void testBuildContact(Long id, String firstName, String middleInitial, String lastName, String email,
			String phoneNumber, String phoneType, boolean valid) throws Exception {
		contact = Contact.newBuilder().setId(id).setFirstName(firstName).setMiddleInitial(middleInitial)
				.setLastName(lastName).setEmail(email).addPhone(Phone.newBuilder().setPhoneNumber(phoneNumber)
						.setPhoneType(PhoneType.valueOf(phoneType)).build())
				.setValid(valid).build();
		assertNotNull(contact);
		assertAll(() -> assertEquals(id, contact.getId()),
				() -> assertEquals(firstName, contact.getFirstName()),
				() -> assertEquals(middleInitial, contact.getMiddleInitial()),
				() -> assertEquals(lastName, contact.getLastName()),
				() -> assertEquals(email, contact.getEmail()),
				() -> assertEquals(phoneNumber, contact.getPhone(0).getPhoneNumber()),
				() -> assertEquals(PhoneType.valueOf(phoneType), contact.getPhone(0).getPhoneType()),
				() -> assertEquals(valid, contact.getValid()));
		logger.debug("Message: {}", JsonFormat.printer().print(contact));
	}

}
