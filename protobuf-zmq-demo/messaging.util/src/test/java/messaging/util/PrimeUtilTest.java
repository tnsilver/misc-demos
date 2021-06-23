package messaging.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class PrimeUtilTest tests the functionality of the {@link PrimeUtil}
 * utility class
 *
 * @author T.Silverman
 *
 */
class PrimeUtilTest {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesTest.class);

	@BeforeEach
	public void beforeEach(TestInfo info) throws Exception {
		logger.info("\n\nENTERING {}\n", info.getDisplayName());
	}

	@ParameterizedTest(name = "test next prime")
	@ValueSource(strings = { "10000000000000000000000000000000000000000000000000",
			"9999999999999999999999999999999999999999999999999" })
	void testNextPrime(String start) {
		BigInteger actual = PrimeUtil.nextPrime(new BigInteger(start));
		assertNotNull(actual);
		assertTrue(actual.toString().length() >= start.length());
	}

	@ParameterizedTest(name = "test find prime")
	@ValueSource(ints = { 16, 32, 64, 128, 256, 512 })
	void testFindPrime(int numDigits) {
		BigInteger actual = PrimeUtil.findPrime(numDigits);
		assertNotNull(actual);
		assertEquals(numDigits, actual.toString().length());
	}

	@ParameterizedTest(name = "test random number")
	@ValueSource(ints = { 16, 32, 64, 128, 256, 512 })
	void testRandomNum(int numDigits) {
		BigInteger actual = PrimeUtil.randomNum(numDigits);
		assertNotNull(actual);
		assertEquals(numDigits, actual.toString().length());
	}

	@ParameterizedTest(name = "test random int")
	@ValueSource(ints = { 16, 32, 64, 128 })
	void testRandomInt(int range) {
		Integer actual = PrimeUtil.randomInt(range);
		assertNotNull(actual);
		assertTrue(actual >= 0);
		assertTrue(actual < range);
	}

	@ParameterizedTest(name = "test random index")
	@CsvSource(delimiter = '@', value = { "1,2,3,4,5,6,7,8,9", "a,b,c,d,e,f,g,h" })
	void testRandomIndex(String csv) {
		String[] array = csv.split(",");
		Integer actual = PrimeUtil.randomIndex(array);
		assertNotNull(actual);
		assertTrue(actual >= 0);
		assertTrue(actual < array.length);
	}

	@ParameterizedTest(name = "test random element")
	@CsvSource(delimiter = '@', value = { "1,2,3,4,5,6,7,8,9", "a,b,c,d,e,f,g,h" })
	void testRandomElement(String csv) {
		String[] array = csv.split(",");
		Object actual = PrimeUtil.randomElement(array);
		assertNotNull(actual);
		assertTrue(Arrays.asList(array).contains(actual));
	}
	// primesStream

	@ParameterizedTest(name = "test primes stream")
	@ValueSource(ints = { 16, 32, 64, 128, 256, 514 })
	void testPrimesStream(int primes) throws Exception {
		int digits = 10;
		Stream<BigInteger> actual = PrimeUtil.primesStream(digits, primes);
		assertNotNull(actual);
		AtomicInteger primesCounter = new AtomicInteger();
		actual.forEach(p -> {
			assertTrue(p.toString().length() >= digits);
			primesCounter.addAndGet(1);
		});
		assertEquals(primes, primesCounter.get());
	}

	@ParameterizedTest(name = "test primes list")
	@ValueSource(ints = { 16, 32, 64, 128, 256, 514 })
	void testPrimesList(int primes) throws Exception {
		int digits = 10;
		List<BigInteger> actual = PrimeUtil.primesList(digits, primes);
		assertNotNull(actual);
		AtomicInteger primesCounter = new AtomicInteger();
		actual.forEach(p -> {
			assertTrue(p.toString().length() >= digits);
			primesCounter.addAndGet(1);
		});
		assertEquals(primes, primesCounter.get());
	}

	@ParameterizedTest(name = "test primes array")
	@ValueSource(ints = { 16, 32, 64, 128, 256, 514 })
	void testPrimesArray(int primes) throws Exception {
		int digits = 10;
		BigInteger[] actual = PrimeUtil.primesArray(digits, primes);
		for (BigInteger prime : actual)
			assertTrue(prime.toString().length() >= digits);
		assertEquals(primes, actual.length);
	}

}
