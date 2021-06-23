package messaging.protos;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.Timestamps;

import messaging.protos.AddressProtos.Address;
import messaging.protos.AddressProtos.Address.AddressType;
import messaging.protos.AddressProtos.Address.OneofStateCase;
import messaging.protos.CanadianProvinceEnumProtos.CanadianProvinceEnum.Province;
import messaging.protos.GermanLandEnumProtos.GermanLandEnum.Land;
import messaging.protos.PersonProtos.Person;
import messaging.protos.USStateEnumProtos.USStateEnum.State;

class PersonTest {

	private static final String RFC3339FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final Logger logger = LoggerFactory.getLogger(PersonTest.class);
	private Person person; /* = Person.getDefaultInstance(); */

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
		person = Person.newBuilder().build();
		assertNotNull(person);
	}

	@Test
	@DisplayName("test default build")
	void testDefaultBuild() throws Exception {
		assertNotNull(person);
		logger.debug("Message: {}", JsonFormat.printer().print(person));
	}

	@Test
	@DisplayName("test descriptor")
	void testDescriptor() throws Exception {
		Descriptor descriptor = Person.getDescriptor();
		List<String> fieldNames = descriptor.getFields().stream().map(f -> {
			System.out.printf("name: %d) %s (%s)%n", f.getNumber(), f.getName(), f.getJavaType());
			return f.getName();
		}).collect(Collectors.toList());
		assertAll(() -> assertTrue(fieldNames.contains("id")),
				() -> assertTrue(fieldNames.contains("guid")),
				() -> assertTrue(fieldNames.contains("first_name")),
				() -> assertTrue(fieldNames.contains("middle_initial")),
				() -> assertTrue(fieldNames.contains("last_name")),
				() -> assertTrue(fieldNames.contains("date_of_birth")),
				() -> assertTrue(fieldNames.contains("marital_status")),
				() -> assertTrue(fieldNames.contains("num_of_children")));
	}

	@ParameterizedTest(name = "test build person")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3")
	void testBuildPerson(Long id, String guid, String firstName, String middleInitial, String lastName,
			String dateOfBirth, String maritalStatus, Integer numOfChildren) throws Exception {
		Person actual = Person.newBuilder().setId(id).setGuid(guid).setFirstName(firstName)
				.setMiddleInitial(middleInitial).setDateOfBirth(Timestamps.parse(dateOfBirth)).setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus)).setNumOfChildren(numOfChildren).build();
		assertNotNull(actual);
		assertAll(() -> assertEquals(id, actual.getId()),
				() -> assertEquals(guid, actual.getGuid()),
				() -> assertEquals(firstName, actual.getFirstName()),
				() -> assertEquals(middleInitial, actual.getMiddleInitial()),
				() -> assertEquals(lastName, actual.getLastName()),
				() -> assertEquals(Timestamps.parse(dateOfBirth), actual.getDateOfBirth()),
				() -> assertEquals(Person.MaritalStatus.valueOf(maritalStatus), actual.getMaritalStatus()),
				() -> assertEquals(numOfChildren, actual.getNumOfChildren()));
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test local date time to timestamp conversion")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3")
	void testLocalDateTimeToTimestampConversion(Long id, String guid, String firstName, String middleInitial,
			String lastName, String dateOfBirth, String maritalStatus, Integer numOfChildren) throws Exception {
		Person actual = Person.newBuilder().setId(id).setGuid(guid).setFirstName(firstName)
				.setMiddleInitial(middleInitial).setDateOfBirth(Timestamps.parse(dateOfBirth)).setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus)).setNumOfChildren(numOfChildren).build();
		assertNotNull(actual);
		LocalDateTime localDateTime = LocalDateTime.parse(dateOfBirth, DateTimeFormatter.ofPattern(RFC3339FORMAT));
		Timestamp expected = fromLocalDateTime(localDateTime);
		assertEquals(expected, actual.getDateOfBirth());
		logger.debug("local date time: {}", localDateTime);
	}

	@ParameterizedTest(name = "test timestamp to local date conversion")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3")
	void testTimestampToLocalDateTimeConversion(Long id, String guid, String firstName, String middleInitial,
			String lastName, String dateOfBirth, String maritalStatus, Integer numOfChildren) throws Exception {
		Person actual = Person.newBuilder().setId(id).setGuid(guid).setFirstName(firstName)
				.setMiddleInitial(middleInitial).setDateOfBirth(Timestamps.parse(dateOfBirth)).setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus)).setNumOfChildren(numOfChildren).build();
		assertNotNull(actual);
		LocalDateTime localDateTime = LocalDateTime.parse(dateOfBirth, DateTimeFormatter.ofPattern(RFC3339FORMAT));
		assertEquals(localDateTime, toLocalDateTime(actual.getDateOfBirth()));
		logger.debug("local date time: {}", localDateTime);
	}

	@ParameterizedTest(name = "test parse person")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3")
	void testParsePerson(Long id, String guid, String firstName, String middleInitial, String lastName,
			String dateOfBirth, String maritalStatus, Integer numOfChildren) throws Exception {
		Person actual = Person.newBuilder().setId(id).setGuid(guid).setFirstName(firstName)
				.setMiddleInitial(middleInitial).setDateOfBirth(Timestamps.parse(dateOfBirth)).setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus)).setNumOfChildren(numOfChildren).build();
		assertFalse(actual == null);
		Person expected = Person.parseFrom(actual.toByteArray());
		assertEquals(expected, actual);
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test parse person with address")
	@CsvSource("1,0-2433257-9,Tomer,S,Nir Silverman,1969-02-06T10:15:20.021Z,SINGLE,3,1,Boston City Hall,1 City Hall Square #500,Boston,MA,02201,United States,BUSINESS")
	void testParsePersonWithAddress(Long id, String guid, String firstName, String middleInitial, String lastName,
			String dateOfBirth, String maritalStatus, Integer numOfChildren, Long addressId, String addressLine1,
			String addressLine2, String city, String state, String zip, String country, String addressType)
			throws Exception {
		Address address = Address.newBuilder().setId(addressId).setAddressLine1(addressLine1)
				.setAddressLine2(addressLine2).setCity(city).setState(State.valueOf(state)).setCountry(country)
				.setZip(zip).setAddressType(AddressType.valueOf(addressType)).build();
		Person actual = Person.newBuilder().setId(id).setGuid(guid).setFirstName(firstName)
				.setMiddleInitial(middleInitial).setDateOfBirth(Timestamps.parse(dateOfBirth)).setLastName(lastName)
				.setMaritalStatus(Person.MaritalStatus.valueOf(maritalStatus)).setNumOfChildren(numOfChildren)
				.addAddress(address).build();
		assertFalse(actual == null);
		Person expected = Person.parseFrom(actual.toByteArray());
		Address actualAddress = actual.getAddress(0);
        Object test = null;
		switch (actualAddress.getOneofStateCase()) {
			case PROVINCE: test = actualAddress.getProvince() != null ? Province.valueOf(state) : null; break;
			case LAND: test = actualAddress.getLand() != null ? Land.valueOf(state) : null; break;
			case STATE: test = actualAddress.getState() != null ? State.valueOf(state) : null; break;
			default: test = OneofStateCase.ONEOFSTATE_NOT_SET;
		};
		final Object actualState = test;
		assertAll(() -> assertEquals(addressId, actual.getAddress(0).getId()),
				() -> assertEquals(addressLine1, actual.getAddress(0).getAddressLine1()),
				() -> assertEquals(addressLine2, actual.getAddress(0).getAddressLine2()),
				() -> assertEquals(city, actual.getAddress(0).getCity()),
				() -> assertEquals(actualState, actual.getAddress(0).getState()),
				() -> assertEquals(country, actual.getAddress(0).getCountry()),
				() -> assertEquals(zip, actual.getAddress(0).getZip()),
				() -> assertEquals(AddressType.valueOf(addressType), actual.getAddress(0).getAddressType()));
		assertEquals(expected, actual);
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	private static Timestamp fromLocalDateTime(LocalDateTime localDateTime) {
		Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
		return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
	}

	private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()),
				ZoneId.of("UTC"));
	}
	// Illegal reflective access by com.google.protobuf.UnsafeUtil
	// (file:/home/patricia/.m2/repository/com/google/protobuf/protobuf-java/3.6.1/protobuf-java-3.6.1.jar)
	// to field java.nio.Buffer.address

	/*
	 * jvm_flags = [ # quiet warnings from com.google.protobuf.UnsafeUtil, # see:
	 * https://github.com/google/protobuf/issues/3781
	 * "-XX:+IgnoreUnrecognizedVMOptions",
	 * "--add-opens=java.base/java.nio=ALL-UNNAMED",
	 * "--add-opens=java.base/java.lang=ALL-UNNAMED", ],
	 */
}
