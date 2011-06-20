package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import com.sleepycat.je.OperationStatus;

public class BerkeleyDBHttpCacheStorageException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6381754475995443701L;
	private final String key;
	private final OperationStatus status;

	public BerkeleyDBHttpCacheStorageException(final String key,
			final OperationStatus status) {
		this.key = key;
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public OperationStatus getStatus() {
		return status;
	}

}
