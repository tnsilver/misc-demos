package messaging.util;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class Properties loads and caches the resource file application
 * properties and serves properties to classes.
 *
 * @author T.Silverman
 *
 */
public final class Properties {

	private static final Logger logger = LoggerFactory.getLogger(Properties.class);
	private static final String DEFAULT_PROP_FILE = "application.properties";
	private java.util.Properties cache;
	private static volatile Properties INSTANCE;

	private Properties() {
		try {
			init();
		} catch (Exception ex) {
			logger.error("{}", ex);
		}
	}

	/**
	 * loads the cache of properties
	 *
	 * @throws Exception
	 */
	private void init() throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		cache = new java.util.Properties();
		try (InputStream in = loader.getResourceAsStream(DEFAULT_PROP_FILE)) {
			cache.load(in);
		}
	}

	/**
	 * returns the property value associated with the given {@code key} or the given
	 * {@code defaultValue} if there's no such key
	 *
	 * @param key          the property key
	 * @param defaultValue a default value.
	 * @return the property value associated with the given {@code key} or the given
	 */
	public final String getProperty(String key, String defaultValue) {
		return cache.getProperty(key, defaultValue);
	}

	/**
	 * returns the property value associates with the given {@code key} as int, or
	 * the given int {@code defaultValue} if there's no such key.
	 *
	 * @param key          the property key
	 * @param defaultValue a default value.
	 * @return the property value associates with the given {@code key} as int, or
	 *         the given int {@code defaultValue} if there's no such key.
	 * @throws NumberFormatException if the property exists but cannot be converted
	 *                               to an int
	 */
	public final int getIntProperty(String key, int defaultValue) throws NumberFormatException {
		return Integer.parseInt(cache.getProperty(key, "" + defaultValue));
	}

	/**
	 * returns the property value associated with the given {@code key} or null if
	 * there's no such key
	 *
	 * @param key the property key
	 * @return the property value associated with the given {@code key} or null
	 */
	public final String getProperty(String key) {
		return cache.getProperty(key);
	}

	/**
	 * get the singleton instance of this class
	 *
	 * @return the singleton instance of this class
	 */
	public static Properties getInstance() {
		if (INSTANCE == null) {
			synchronized (Properties.class) {
				if (INSTANCE == null)
					INSTANCE = new Properties();
			}
		}
		return INSTANCE;
	}

}
