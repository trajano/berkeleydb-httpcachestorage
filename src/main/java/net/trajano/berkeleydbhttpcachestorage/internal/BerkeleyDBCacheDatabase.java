package net.trajano.berkeleydbhttpcachestorage.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.BerkeleyDBHttpCacheStorageException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheEntrySerializer;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.impl.client.cache.DefaultHttpCacheEntrySerializer;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * This wraps a {@link Database} to provide methods that would match against
 * {@link org.apache.http.client.cache.HttpCacheStorage} with an extra parameter
 * for the transaction. Non-transactional databases will pass in
 * <code>null</code> as the {@link Transaction} parameter.
 * 
 * @author Archimedes Trajano
 * 
 */
public class BerkeleyDBCacheDatabase {
	/**
	 * Berkeley DB.
	 */
	private final Database database;

	/**
	 * This serializes an {@link HttpCacheEntry}.
	 */
	private final HttpCacheEntrySerializer serializer;

	/**
	 * Constructs the cache.
	 * 
	 * @param database
	 *            database to wrap.
	 */
	public BerkeleyDBCacheDatabase(final Database database) {
		this.database = database;
		serializer = new DefaultHttpCacheEntrySerializer();
	}

	/**
	 * Gets an entry on the database.
	 * 
	 * @see org.apache.http.client.cache.HttpCacheStorage#getEntry(String)
	 * @param txn
	 *            transaction
	 * @param key
	 *            key
	 * @return the cache object. May be <code>null</code> if the record is not
	 *         in the cache.
	 * @throws IOException
	 *             I/O error has occurred.
	 */
	public HttpCacheEntry getEntry(final Transaction txn, final String key)
			throws IOException {
		final DatabaseEntry data = new DatabaseEntry();
		final OperationStatus status = database.get(txn, new DatabaseEntry(
				KeyUtil.entityKey(key)), data, LockMode.READ_COMMITTED);
		if (status == OperationStatus.NOTFOUND) {
			return null;
		} else if (status == OperationStatus.SUCCESS) {
			return serializer
					.readFrom(new ByteArrayInputStream(data.getData()));
		} else {
			throw new BerkeleyDBHttpCacheStorageException(key, status);
		}
	}

	/**
	 * Puts an entry on the database.
	 * 
	 * @see org.apache.http.client.cache.HttpCacheStorage#putEntry(String,
	 *      HttpCacheEntry)
	 * @param txn
	 *            transaction
	 * @param key
	 *            key
	 * @param entry
	 *            the cache entry
	 * @throws IOException
	 *             I/O error has occurred.
	 */
	public void putEntry(final Transaction txn, final String key,
			final HttpCacheEntry entry) throws IOException {
		final DatabaseEntry data = new DatabaseEntry();
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeTo(entry, baos);
		data.setData(baos.toByteArray());
		final OperationStatus status = database.put(txn, new DatabaseEntry(
				KeyUtil.entityKey(key)), data);
		if (status != OperationStatus.SUCCESS) {
			throw new BerkeleyDBHttpCacheStorageException(key, status);
		}
	}

	/**
	 * Removes an entry on the database.
	 * 
	 * @see org.apache.http.client.cache.HttpCacheStorage#removeEntry(String)
	 * @param txn
	 *            transaction
	 * @param key
	 *            key
	 * @throws IOException
	 *             I/O error has occurred.
	 */
	public void removeEntry(final Transaction txn, final String key)
			throws IOException {
		final OperationStatus status = database.delete(txn, new DatabaseEntry(
				KeyUtil.entityKey(key)));
		if (status != OperationStatus.SUCCESS
				&& status != OperationStatus.NOTFOUND) {
			throw new BerkeleyDBHttpCacheStorageException(key, status);
		}
	}

	/**
	 * Updates the entry on the database. It does this using a {@link Cursor}
	 * and an update rather than doing a get and put for efficiency.
	 * 
	 * @see org.apache.http.client.cache.HttpCacheStorage#updateEntry(String,
	 *      HttpCacheUpdateCallback)
	 * @param txn
	 *            transaction
	 * @param key
	 *            key
	 * @param callback
	 *            callback
	 * @throws IOException
	 *             I/O error has occurred.
	 */
	public void updateEntry(final Transaction txn, final String key,
			final HttpCacheUpdateCallback callback) throws IOException {

		final DatabaseEntry data = new DatabaseEntry();
		// Made not final as this will get replaced.
		HttpCacheEntry entry;
		final Cursor cursor = database.openCursor(txn,
				CursorConfig.READ_COMMITTED);
		try {
			final OperationStatus status = cursor.getSearchKey(
					new DatabaseEntry(KeyUtil.entityKey(key)), data,
					LockMode.READ_COMMITTED);
			if (status == OperationStatus.NOTFOUND) {
				entry = null;
			} else if (status == OperationStatus.SUCCESS) {
				entry = serializer.readFrom(new ByteArrayInputStream(data
						.getData()));
			} else {

				throw new BerkeleyDBHttpCacheStorageException(key, status);
			}
			entry = callback.update(entry);

			final ByteArrayOutputStream baos = new ByteArrayOutputStream(
					data.getSize());
			serializer.writeTo(entry, baos);
			data.setData(baos.toByteArray());
			final OperationStatus putStatus = cursor.putCurrent(data);
			if (putStatus != OperationStatus.SUCCESS) {
				throw new BerkeleyDBHttpCacheStorageException(key, putStatus);
			}
		} finally {
			cursor.close();
		}
	}
}
