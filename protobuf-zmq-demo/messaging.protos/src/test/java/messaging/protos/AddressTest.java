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

import messaging.protos.AddressProtos.Address;
import messaging.protos.AddressProtos.Address.AddressType;
import messaging.protos.AddressProtos.Address.OneofStateCase;
import messaging.protos.CanadianProvinceEnumProtos.CanadianProvinceEnum.Province;
import messaging.protos.GermanLandEnumProtos.GermanLandEnum.Land;
import messaging.protos.USStateEnumProtos.USStateEnum.State;

class AddressTest {

	private static final Logger logger = LoggerFactory.getLogger(AddressTest.class);
	private Address address;

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
		address = Address.newBuilder().build();
	}

	@Test
	@DisplayName("test default build")
	void testDefaultBuild() throws Exception {
		assertNotNull(address);
		logger.debug("Message: {}", JsonFormat.printer().print(address));
	}

	@Test
	@DisplayName("test descriptor")
	void testDescriptor() throws Exception {
		Descriptor descriptor = Address.getDescriptor();
		List<String> fieldNames = descriptor.getFields().stream().map(f -> {
			System.out.printf("name: %d) %s (%s)%n", f.getNumber(), f.getName(), f.getJavaType());
			return f.getName();
		}).collect(Collectors.toList());
		assertAll(() -> assertTrue(fieldNames.contains("id")),
				() -> assertTrue(fieldNames.contains("address_line_1")),
				() -> assertTrue(fieldNames.contains("address_line_2")),
				() -> assertTrue(fieldNames.contains("city")),
				() -> assertTrue(fieldNames.contains("state")),
				() -> assertTrue(fieldNames.contains("province")),
				() -> assertTrue(fieldNames.contains("land")),
				() -> assertTrue(fieldNames.contains("zip")),
				() -> assertTrue(fieldNames.contains("country")),
				() -> assertTrue(fieldNames.contains("address_type")));
	}

	@ParameterizedTest(name = "test build Canadian address")
	@CsvSource("3,Montreal City Hall,275 Notre-Dame St.,Montreal,QC,H2Y 1C6,Canada,true")
	void testBuildCAAddress(Long id, String addressLine1, String addressLine2, String city, String state, String zip,
			String country) throws Exception {
		logger.info("addressLine2: {}", addressLine2);
		Address actual = Address.newBuilder().setId(id).setAddressLine1(addressLine1)
				.setAddressLine2(String.valueOf(addressLine2.toCharArray())).setCity(city)
				.setProvince(Province.valueOf(state)).setCountry(country).setZip(zip)
				.setAddressType(AddressType.BUSINESS).build();
		assertNotNull(actual);
		Object test = null;
		switch (actual.getOneofStateCase()) {
			case PROVINCE: test = actual.getProvince() != null ? Province.valueOf(state) : null; break;
			case LAND: test = actual.getLand() != null ? Land.valueOf(state) : null; break;
			case STATE: test = actual.getState() != null ? State.valueOf(state) : null; break;
			default: test = OneofStateCase.ONEOFSTATE_NOT_SET;
		};
		final Object expected = test;
		// OneofDescriptor oneof =
		// actual.getDescriptorForType().getOneofs().stream().filter(ood ->
		// ood.getName().equals("oneof_state")).findFirst().orElse(null);
		assertAll(() -> assertEquals(id, actual.getId()),
				() -> assertEquals(addressLine1, actual.getAddressLine1()),
				() -> assertEquals(addressLine2, actual.getAddressLine2()),
				() -> assertEquals(city, actual.getCity()),
				() -> assertEquals(expected.toString(), state),
				() -> assertEquals(country, actual.getCountry()),
				() -> assertEquals(zip, actual.getZip()),
				() -> assertEquals(AddressType.BUSINESS, actual.getAddressType()));
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test build US address")
	@CsvSource("1,Boston City Hall,1 City Hall Square #500,Boston,MA,02201,United States,true")
	void testBuildUSAddress(Long id, String addressLine1, String addressLine2, String city, String state, String zip,
			String country) throws Exception {
		Address actual = Address.newBuilder().setId(id).setAddressLine1(addressLine1).setAddressLine2(addressLine2)
				.setCity(city).setState(State.valueOf(state)).setCountry(country).setZip(zip)
				.setAddressType(AddressType.BUSINESS).build();
		assertNotNull(actual);
		Object test = null;
		switch (actual.getOneofStateCase()) {
			case PROVINCE: test = actual.getProvince() != null ? Province.valueOf(state) : null; break;
			case LAND: test = actual.getLand() != null ? Land.valueOf(state) : null; break;
			case STATE: test = actual.getState() != null ? State.valueOf(state) : null; break;
			default: test = OneofStateCase.ONEOFSTATE_NOT_SET;
		};
		final Object expected = test;
		// OneofDescriptor oneof =
		// actual.getDescriptorForType().getOneofs().stream().filter(ood ->
		// ood.getName().equals("oneof_state")).findFirst().orElse(null);
		assertAll(() -> assertEquals(id, actual.getId()),
				() -> assertEquals(addressLine1, actual.getAddressLine1()),
				() -> assertEquals(addressLine2, actual.getAddressLine2()),
				() -> assertEquals(city, actual.getCity()),
				() -> assertEquals(expected.toString(), state),
				() -> assertEquals(country, actual.getCountry()),
				() -> assertEquals(zip, actual.getZip()),
				() -> assertEquals(AddressType.BUSINESS, actual.getAddressType()));
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test build German address")
	@CsvSource("2,Berlin City Hall,Rathausstraße 15,Berlin,BE,10178,Germany,false")
	void testBuildDEAddress(Long id, String addressLine1, String addressLine2, String city, String state, String zip,
			String country) throws Exception {
		logger.info("addressLine2: {}", addressLine2);
		Address actual = Address.newBuilder().setId(id).setAddressLine1(addressLine1)
				.setAddressLine2(String.valueOf(addressLine2.toCharArray())).setCity(city).setLand(Land.valueOf(state))
				.setCountry(country).setZip(zip).setAddressType(AddressType.BUSINESS).build();
		assertNotNull(actual);
		Object test = null;
		switch (actual.getOneofStateCase()) {
			case PROVINCE: test = actual.getProvince() != null ? Province.valueOf(state) : null; break;
			case LAND: test = actual.getLand() != null ? Land.valueOf(state) : null; break;
			case STATE: test = actual.getState() != null ? State.valueOf(state) : null; break;
			default: test = OneofStateCase.ONEOFSTATE_NOT_SET;
		};
		final Object expected = test;
		// OneofDescriptor oneof =
		// actual.getDescriptorForType().getOneofs().stream().filter(ood ->
		// ood.getName().equals("oneof_state")).findFirst().orElse(null);
		assertAll(() -> assertEquals(id, actual.getId()),
				() -> assertEquals(addressLine1, actual.getAddressLine1()),
				() -> assertEquals(addressLine2, actual.getAddressLine2()),
				() -> assertEquals(city, actual.getCity()),
				() -> assertEquals(expected.toString(), state),
				() -> assertEquals(country, actual.getCountry()),
				() -> assertEquals(zip, actual.getZip()),
				() -> assertEquals(AddressType.BUSINESS, actual.getAddressType()));
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}

	@ParameterizedTest(name = "test build Israeli address")
	@CsvSource("4,תומר סילברמן,הנביאים 7,תל אביב,6435609,ישראל,true")
	void testBuildILAddress(Long id, String addressLine1, String addressLine2, String city, String zip, String country)
			throws Exception {
		Address actual = Address.newBuilder().setId(id).setAddressLine1(addressLine1).setAddressLine2(addressLine2)
				.setCity(city).setCountry(country).setZip(zip).setAddressType(AddressType.RESIDENTIAL).build();
		assertNotNull(actual);
		assertAll(() -> assertEquals(id, actual.getId()),
				() -> assertEquals(addressLine1, actual.getAddressLine1()),
				() -> assertEquals(addressLine2, actual.getAddressLine2()),
				() -> assertEquals(city, actual.getCity()),
				() -> assertEquals(country, actual.getCountry()),
				() -> assertEquals(zip, actual.getZip()),
				() -> assertEquals(AddressType.RESIDENTIAL, actual.getAddressType()));
		logger.debug("Message: {}", JsonFormat.printer().print(actual));
	}
}
