package messaging.protos.util;

import java.io.UnsupportedEncodingException;

/**
 * The enum Filter contains message filter values
 *
 * @author T.Silverman
 *
 */
public enum Filter {

	US("US".getBytes()), DE("DE".getBytes()), CA("CA".getBytes()), IL("IL".getBytes());

	private byte[] bytes;

	/**
	 * get this value as a byte array
	 *
	 * @return this value as a byte array
	 */
	public byte[] toBytes() {
		return bytes;
	}

	/**
	 * get this value as string
	 *
	 * @return this value as string
	 */
	public String value() {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return this.toString();
		}
	}

	private Filter(byte[] bytes) {
		this.bytes = bytes;
	}

}
