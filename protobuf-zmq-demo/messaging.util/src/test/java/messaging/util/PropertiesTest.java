package messaging.util;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class PropertiesTest tests the functionality of the utility
 * {@link Properties} class
 *
 * @author T.Silverman
 *
 */
class PropertiesTest {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesTest.class);

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
	}

	@Test
	@DisplayName("test get instance")
	void testGetInstance() throws Exception {
		Properties actual = Properties.getInstance();
		assertNotNull(actual);
	}

	@Test
	@DisplayName("test get instance is singleton")
	void testGetInstanceIsSingleton() throws Exception {
		Properties actual = Properties.getInstance();
		assertNotNull(actual);
		Properties expected = Properties.getInstance();
		assertNotNull(expected);
		assertEquals(expected, actual);
		assertSame(expected, actual);
	}

	@ParameterizedTest(name = "test get property")
	@CsvSource("TCP,TCP_5556,TCP_5557,TCP_5558,INPROC,IPC_DEMO")
	void testGetProperty(String key) throws Exception {
		Properties properties = Properties.getInstance();
		assertNotNull(properties.getProperty(key, null));
	}

	@ParameterizedTest(name = "test get property with default")
	@CsvSource("TCP,TCP_5556,TCP_5557,TCP_5558,INPROC,IPC_DEMO")
	void testGetPropertyWithDefault(String key) throws Exception {
		Properties properties = Properties.getInstance();
		assertNotNull(properties.getProperty(key, key));
	}

	@ParameterizedTest(name = "test get property with default")
	@CsvSource("TCPB,TCP_5556B,TCP_5557B,TCP_5558B,INPROCB,IPC_DEMOB")
	void testGetBogusPropertyWithDefault(String key) throws Exception {
		Properties properties = Properties.getInstance();
		assertNotNull(properties.getProperty(key, key));
	}

	@ParameterizedTest(name = "test get bogus property")
	@CsvSource("TCPB,TCP_5556B,TCP_5557B,TCP_5558B,INPROCB,IPC_DEMOB")
	void testGetBogusProperty(String key) throws Exception {
		Properties properties = Properties.getInstance();
		assertNull(properties.getProperty(key));
	}

	@ParameterizedTest(name = "test get int property")
	@CsvSource("CONTEXT_THREADPOOL_SIZE")
	void testGetIntProperty(String key) throws Exception {
		Properties properties = Properties.getInstance();
		int value = properties.getIntProperty(key, 1000);
		logger.debug("the value of property {} is {}", key, value);
		assertFalse(value == 1000 || value == 0);
	}

}
