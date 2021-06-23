package messaging.protos.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.util.JsonFormat;

import messaging.protos.AddressProtos.Address;
import messaging.protos.ContactProtos.Contact;
import messaging.protos.PersonProtos.Person;
import messaging.protos.util.MessageUtil;

/**
 * The class MessageUtilsTest tests the functionality of the
 * {@link MessageUtilsTest} utility class
 *
 * @author T.Silverman
 *
 */
class MessageUtilsTest {

	private static final Logger logger = LoggerFactory.getLogger(MessageUtilsTest.class);

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
	}

	@Test
	@RepeatedTest(5)
	@DisplayName("test get random address")
	void testRandomAddress() throws Exception {
		Address actual = MessageUtil.randomAddress();
		assertNotNull(actual);
		logger.debug("Message: {}",JsonFormat.printer().print(actual));
	}

	@Test
	@RepeatedTest(5)
	@DisplayName("test get random contact")
	void testRandomContact() throws Exception {
		Contact actual = MessageUtil.randomContact();
		assertNotNull(actual);
		logger.debug("Message: {}",JsonFormat.printer().print(actual));
	}

	@Test
	@RepeatedTest(5)
	@DisplayName("test get random person")
	void testRandomPerson() throws Exception {
		Person actual = MessageUtil.randomPerson();
		assertNotNull(actual);
		logger.debug("Message: {}",JsonFormat.printer().print(actual));
	}

}
