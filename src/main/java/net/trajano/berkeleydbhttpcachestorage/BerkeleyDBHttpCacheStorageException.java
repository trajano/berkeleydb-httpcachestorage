package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;
import java.text.MessageFormat;

import net.trajano.berkeleydbhttpcachestorage.internal.Messages;

import com.sleepycat.je.OperationStatus;

/**
 * Signals that a database operation exception has occurred. This is primarily
 * used when an unexpected {@link OperationStatus} was returned by a
 * {@link com.sleepycat.je.Database} operation.
 * 
 * @author Archimedes Trajano
 * 
 */
public class BerkeleyDBHttpCacheStorageException extends IOException {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 6381754475995443701L;
	/**
	 * Key.
	 */
	private final String key;
	/**
	 * Database {@link OperationStatus}.
	 */
	private final OperationStatus status;

	/**
	 * Constructs the exception.
	 * 
	 * @param key
	 *            key
	 * @param status
	 *            operation status.
	 */
	public BerkeleyDBHttpCacheStorageException(final String key,
			final OperationStatus status) {
		this.key = key;
		this.status = status;
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return MessageFormat.format(
				Messages.getString("BerkeleyDBHttpCacheStorageException.msg"), //$NON-NLS-1$
				key, status);
	}

	/**
	 * Gets the operation status.
	 * 
	 * @return operation status.
	 */
	public OperationStatus getStatus() {
		return status;
	}
}
