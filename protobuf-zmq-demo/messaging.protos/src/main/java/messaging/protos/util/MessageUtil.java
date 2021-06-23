package messaging.protos.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.google.protobuf.util.Timestamps;

import messaging.protos.AddressProtos.Address;
import messaging.protos.AddressProtos.Address.AddressType;
import messaging.protos.CanadianProvinceEnumProtos.CanadianProvinceEnum.Province;
import messaging.protos.ContactProtos.Contact;
import messaging.protos.ContactProtos.Contact.Phone;
import messaging.protos.ContactProtos.Contact.Phone.PhoneType;
import messaging.protos.ErrorMessageProtos.ErrorMessage;
import messaging.protos.GermanLandEnumProtos.GermanLandEnum.Land;
import messaging.protos.PersonProtos.Person;
import messaging.protos.USStateEnumProtos.USStateEnum.State;

/**
 * The class Message Utils is a utility for generating random protobuf messages
 *
 * @author T.Silverman
 *
 */
final public class MessageUtil {

	//private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

	private MessageUtil() {
		super();
	}

	/**
	 * Utility method to build an error message
	 *
	 * @param message   the error message
	 * @param className the exception class name
	 * @return an ErrorMessage
	 */
	public static ErrorMessage createErrorMessage(String message, String className) {
		return ErrorMessage.newBuilder().setMessage(message).setExceptionClass(className).build();
	}

	/**
	 * get a random filter value as string
	 *
	 * @return a random filter value as string
	 */
	public static String randFilterStr() {
		int rand = ThreadLocalRandom.current().nextInt(0, Filter.values().length);
		return Filter.values()[rand].toString();
	}

	/**
	 * gets a default address protobuf
	 *
	 * @return a default address protobuf
	 */
	public static Address defaultAddress() {
		return Address.newBuilder().setFilter(randFilterStr()).setId(5L).setAddressLine1("Santa Claus")
				.setAddressLine2("25 S. Santa Claus Lane").setCity("North Pole").setState(State.AK)
				.setCountry("United States").setZip("99705").setAddressType(AddressType.RESIDENTIAL).build();
	}

	/**
	 * returns a list of 5 address protobuf messages
	 *
	 * @return a list of 5 address protobuf messages
	 */
	private static List<Address> addresses() {
		Address[] addresses = {
				Address.newBuilder().setFilter(randFilterStr()).setId(1L).setAddressLine1("Boston City Hall")
						.setAddressLine2("1 City Hall Square #500").setCity("Boston").setState(State.MA)
						.setCountry("United States").setZip("02201").setAddressType(AddressType.BUSINESS).build(),
				Address.newBuilder().setFilter(randFilterStr()).setId(2L).setAddressLine1("Berlin City Hall")
						.setAddressLine2("Rathausstraße 15").setCity("Berlin").setLand(Land.BE).setCountry("Germany")
						.setZip("10178").setAddressType(AddressType.BUSINESS).build(),
				Address.newBuilder().setFilter(randFilterStr()).setId(3L).setAddressLine1("Montreal City Hall")
						.setAddressLine2("275 Notre-Dame St.").setCity("Montreal").setProvince(Province.QC)
						.setCountry("Canada").setZip("H2Y 1C6").setAddressType(AddressType.BUSINESS).build(),
				Address.newBuilder().setFilter(randFilterStr()).setId(4L).setAddressLine1("תומר סילברמן")
						.setAddressLine2("הנביאים 7").setCity("תל-אביב").setCountry("ישראל").setZip("6435609")
						.setAddressType(AddressType.RESIDENTIAL).build(),
				Address.newBuilder().setFilter(randFilterStr()).setId(5L).setAddressLine1("The Smiths")
						.setAddressLine2("111 Cherry Blvd.").setCity("Amherst").setState(State.MA)
						.setCountry("United States").setZip("91428").setAddressType(AddressType.RESIDENTIAL).build() };
		return Arrays.asList(addresses);
	}

	/**
	 * returns a random address protobuf message
	 *
	 * @return a random address protobuf message
	 */
	public static Address randomAddress() {
		List<Address> addresses = addresses();
		int rand = ThreadLocalRandom.current().nextInt(0, addresses.size());
		return addresses.get(rand);
	}

	/**
	 * returns a list of 5 contact protobuf messages
	 *
	 * @return a list of 5 contact protobuf messages
	 */
	private static List<Contact> contacts() {
		Contact[] contacts = {
				Contact.newBuilder().setFilter(randFilterStr()).setId(1L).setFirstName("Marge").setMiddleInitial("M")
						.setLastName("Simpson").setEmail("marge@thesimpsons.com")
						.addPhone(Phone.newBuilder().setPhoneNumber("555-555-1212").setPhoneType(PhoneType.LANDLINE)
								.build())
						.setValid(true).build(),
				Contact.newBuilder().setFilter(randFilterStr()).setId(2L).setFirstName("Homer").setMiddleInitial("H")
						.setLastName("Simpson").setEmail("homer@thesimpsons.com")
						.addPhone(Phone.newBuilder().setPhoneNumber("555-555-2121").setPhoneType(PhoneType.MOBILE)
								.build())
						.setValid(true).build(),
				Contact.newBuilder().setFilter(randFilterStr()).setId(3L).setFirstName("Lisa").setMiddleInitial("L")
						.setLastName("Simpson").setEmail("lisa@thesimpsons.com")
						.addPhone(Phone.newBuilder().setPhoneNumber("555-555-1234").setPhoneType(PhoneType.MOBILE)
								.build())
						.setValid(true).build(),
				Contact.newBuilder().setFilter(randFilterStr()).setId(4L).setFirstName("Bart").setMiddleInitial("B")
						.setLastName("Simpson").setEmail("bart@thesimpsons.com")
						.addPhone(
								Phone.newBuilder().setPhoneNumber("555-555-4321").setPhoneType(PhoneType.HOME).build())
						.setValid(true).build(),
				Contact.newBuilder().setFilter(randFilterStr()).setId(5L).setFirstName("Maggie").setMiddleInitial("M")
						.setLastName("Simpson").setEmail("maggie@thesimpsons.com")
						.addPhone(
								Phone.newBuilder().setPhoneNumber("555-555-4321").setPhoneType(PhoneType.HOME).build())
						.setValid(true).build()};
		return Arrays.asList(contacts);
	}

	/**
	 * returns a random address protobuf message
	 *
	 * @return a random address protobuf message
	 */
	public static Contact randomContact() {
		List<Contact> contacts = contacts();
		int rand = ThreadLocalRandom.current().nextInt(0, contacts.size());
		return contacts.get(rand);
	}

	/**
	 * returns a list of 5 person protobuf messages
	 *
	 * @return a list of 5 person protobuf messages
	 */
	private static List<Person> people() {
		try {
			Person[] people = {
					Person.newBuilder().setFilter(randFilterStr()).setId(1L).setGuid(UUID.randomUUID().toString())
							.setFirstName("Donald").setMiddleInitial("S")
							.setDateOfBirth(Timestamps.parse("1934-06-09T10:15:20.021Z")).setLastName("Duck")
							.setMaritalStatus(Person.MaritalStatus.SINGLE).setNumOfChildren(0)
							.addAddress(randomAddress()).build(),
					Person.newBuilder().setFilter(randFilterStr()).setId(2L).setGuid(UUID.randomUUID().toString())
							.setFirstName("Mickey").setMiddleInitial("M")
							.setDateOfBirth(Timestamps.parse("1934-11-18T23:00:00.000Z")).setLastName("Mouse")
							.setMaritalStatus(Person.MaritalStatus.SINGLE).setNumOfChildren(0)
							.addAddress(randomAddress()).build(),
					Person.newBuilder().setFilter(randFilterStr()).setId(3L).setGuid(UUID.randomUUID().toString())
							.setFirstName("Popeye").setMiddleInitial("S")
							.setDateOfBirth(Timestamps.parse("1929-01-17T12:35:00.000Z")).setLastName("The Sailor")
							.setMaritalStatus(Person.MaritalStatus.DIVORCED).setNumOfChildren(2)
							.addAddress(randomAddress()).build(),
					Person.newBuilder().setFilter(randFilterStr()).setId(4L).setGuid(UUID.randomUUID().toString())
							.setFirstName("Goofy").setMiddleInitial("D")
							.setDateOfBirth(Timestamps.parse("1932-05-25T14:40:01.020Z")).setLastName("Dog")
							.setMaritalStatus(Person.MaritalStatus.SINGLE).setNumOfChildren(0)
							.addAddress(randomAddress()).build(),
					Person.newBuilder().setFilter(randFilterStr()).setId(5L).setGuid(UUID.randomUUID().toString())
							.setFirstName("Elmer").setMiddleInitial("J")
							.setDateOfBirth(Timestamps.parse("1940-03-02T09:31:12.123Z")).setLastName("Fudd")
							.setMaritalStatus(Person.MaritalStatus.WIDOWED).setNumOfChildren(2)
							.addAddress(randomAddress()).build()};
			return Arrays.asList(people);
		} catch (ParseException ex) {
			System.err.printf("%s%n", ex);
			return new ArrayList<Person>();
		}
	}

	/**
	 * returns a random address protobuf message
	 *
	 * @return a random address protobuf message
	 */
	public static Person randomPerson() {
		List<Person> people = people();
		int rand = ThreadLocalRandom.current().nextInt(0, people.size());
		return people.get(rand);
	}

}
