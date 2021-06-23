package messaging.util;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.stream.Collectors.toList;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A few utilities to generate a large random BigInteger, and find the next
 * prime number above a given BigInteger.
 */
public final class PrimeUtil {

	public static final BigInteger TWO = new BigInteger("2");
	/*
	 * Likelihood of false prime is less than 1/2^ERR_VAL. Presumably BigInteger
	 * uses the Miller-Rabin test or equivalent, and thus is NOT fooled by
	 * Carmichael numbers. See Cormen et al.'s Introduction to Algorithms for
	 * details.
	 */
	private static final int ERR_VAL = 100;

	private PrimeUtil() {
		super();
	}

	/**
	 * Recursively finds the next prime number above a threshold.
	 *
	 * @param start the start number
	 * @return the next prime number above the given {@code start}
	 */
	public static BigInteger nextPrime(BigInteger start) {
		if (isEven(start))
			start = start.add(ONE);
		else
			start = start.add(TWO);
		return start.isProbablePrime(ERR_VAL) ? start : nextPrime(start);
	}

	/**
	 * Generates a random number of the given length, then finds the first prime
	 * number above that random number.
	 */
	public static BigInteger findPrime(int numDigits) {
		if (numDigits < 1) {
			numDigits = 1;
		}
		return (nextPrime(randomNum(numDigits)));
	}

	private static boolean isEven(BigInteger n) {
		return (n.mod(TWO).equals(ZERO));
	}

	private static final String[] DIGITS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private static final String[] NO_ZERO_DIGITS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	/**
	 * returns a random digit string, including or excluding zero, depending on
	 * {@code withZero}. if {@code withZero} is true, the random digit may be a
	 * zero, if false, the random digit will never be a zero
	 *
	 * @param withZero if {@code withZero} is true, the random digit may be a zero,
	 *                 if false, the random digit will never be a zero
	 * @return a random decimal digit string
	 */
	private static String randomDigit(boolean withZero) {
		return withZero ? randomElement(DIGITS) : randomElement(NO_ZERO_DIGITS);
	}

	/**
	 * Creates a random big integer where every digit is selected randomly (except
	 * that the first digit cannot be a zero).
	 */
	public static BigInteger randomNum(int numDigits) {
		StringBuilder builder = new StringBuilder(randomDigit(false));
		IntStream.iterate(0, i -> i += 1).limit(numDigits - 1).forEach(i -> builder.append(randomDigit(true)));
		return (new BigInteger(builder.toString()));
	}

	/**
	 * generates an infinite (unbounded) stream of consecutive BigIntegers prime
	 * numbers with the first prime number having {@code digit} number of digits
	 *
	 * @param digits the number of digits the first number in the stream shall have
	 * @return an infinite (unbounded) stream of consecutive BigIntegers prime
	 *         numbers with the first prime number having {@code digit} number of
	 *         digits
	 */
	public static Stream<BigInteger> primesStream(int digits) {
		return Stream.iterate(findPrime(digits), PrimeUtil::nextPrime);
	}

	/**
	 * generates a stream of consecutive BigIntegers prime numbers of {@code primes}
	 * length with the first prime in the stream having {@code digits} digits
	 *
	 * @param digits number of digits of the first prime number in the stream
	 * @param primes number of prime numbers to make
	 * @return a stream of consecutive BigIntegers prime numbers of {@code primes}
	 *         length with the first prime in the stream having {@code digits}
	 *         digits
	 */
	public static Stream<BigInteger> primesStream(int digits, int primes) {
		return primesStream(digits).limit(primes);
	}

	/**
	 * generates a list of consecutive prime numbers of {@code primes} length with
	 * the first prime in the stream having {@code digits} digits
	 *
	 * @param digits number of digits of the first prime number in the stream
	 * @param primes number of prime numbers to make
	 * @return a list of consecutive prime numbers of {@code primes} length with the
	 *         first prime in the stream having {@code digits} digits
	 */
	public static List<BigInteger> primesList(int digits, int primes) {
		return primesStream(digits, primes).collect(toList());
	}

	/**
	 * generates an array of consecutive prime BigIntegers of {@code primes} length
	 * with the first prime in the stream having {@code digits} digits
	 *
	 * @param digits number of digits of the first prime number in the stream
	 * @param primes number of prime numbers to make
	 * @return an array of consecutive BigIntegers prime numbers of {@code primes}
	 *         length with the first prime in the stream having {@code digits}
	 *         digits
	 */
	public static BigInteger[] primesArray(int digits, int primes) {
		return primesStream(digits, primes).toArray(BigInteger[]::new);
	}

	/**
	 * Gets a random int (0 to range-1). randomInt(4) returns any of 0, 1, 2, or 3.
	 */
	public static int randomInt(int range) {
		return ThreadLocalRandom.current().nextInt(range);
	}

	/** Return a random index of an array. */
	public static int randomIndex(Object[] array) {
		return (randomInt(array.length));
	}

	public static <T> T randomElement(T[] array) {
		return (array[randomIndex(array)]);
	}

	/**
	 * Simple command-line program to test. Enter number of digits, and the program
	 * picks a random number of that length and then prints the first 50 prime
	 * numbers above that.
	 */
	public static void main(String[] args) {
		int numDigits;
		try {
			numDigits = Integer.parseInt(args[0]);
		} catch (Exception e) { // No args or illegal arg.
			numDigits = 256;
		}
		StringBuilder prime = new StringBuilder(randomNum(numDigits).toString());
		IntStream.iterate(1, i -> i += 1).limit(51).forEach(i -> {
			System.out.printf("%d] %s%n", i, prime.toString());
			BigInteger currentPrime = nextPrime(new BigInteger(prime.toString()));
			prime.delete(0, prime.length());
			prime.append(currentPrime);
		});
	}

}
