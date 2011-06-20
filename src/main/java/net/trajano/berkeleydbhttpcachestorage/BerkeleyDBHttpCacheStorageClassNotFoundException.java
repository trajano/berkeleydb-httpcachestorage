package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntrySerializer;

/**
 * This is an exception that would wrap a {@link ClassNotFoundException}. This
 * will only occur if the {@link com.sleepycat.je.Database} did not contain
 * {@link org.apache.http.client.cache.HttpCacheEntry} objects and is thrown by
 * {@link HttpCacheEntrySerializer}
 * 
 * @author Archimedes Trajano
 * 
 */
public class BerkeleyDBHttpCacheStorageClassNotFoundException extends
		IOException {
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3944838715115899379L;
	/**
	 * Nested exception.
	 */
	private final ClassNotFoundException e;

	/**
	 * Constructs the exception.
	 * 
	 * @param e
	 *            the class not found exception.
	 */
	public BerkeleyDBHttpCacheStorageClassNotFoundException(
			final ClassNotFoundException e) {
		this.e = e;
	}

	public ClassNotFoundException getNestedException() {
		return e;
	}

}
