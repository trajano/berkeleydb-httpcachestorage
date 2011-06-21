package net.trajano.berkeleydbhttpcachestorage.internal;

/**
 * This utility class creates the key data used in
 * {@link com.sleepycat.je.DatabaseEntry}.
 * 
 * @author trajano
 * 
 */
public final class KeyUtil {
	public static byte[] entityKey(final String key) {
		final byte[] b = key.getBytes();
		final byte[] ret = new byte[b.length + 1];
		System.arraycopy(b, 0, ret, 1, b.length);
		ret[0] = 1;
		return ret;
	}

	public static byte[] resourceKey(final String key) {
		final byte[] b = key.getBytes();
		final byte[] ret = new byte[b.length + 1];
		System.arraycopy(b, 0, ret, 1, b.length);
		ret[0] = 2;
		return ret;
	}

	public static String toString(final byte[] b) {
		final byte[] ret = new byte[b.length - 1];
		System.arraycopy(b, 1, ret, 0, b.length - 1);
		return new String(ret);
	}
}
