package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

public class BerkeleyDBHttpCacheStorageClassNotFoundException extends
		IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3944838715115899379L;
	/**
	 * Nested exception.
	 */
	private final ClassNotFoundException e;

	public BerkeleyDBHttpCacheStorageClassNotFoundException(
			final ClassNotFoundException e) {
		this.e = e;
	}

	public ClassNotFoundException getNestedException() {
		return e;
	}

}
