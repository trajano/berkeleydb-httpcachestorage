package net.trajano.berkeleydbhttpcachestorage.internal;

import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.BerkleyDBHttpCacheStorageException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

public class BerkeleyDBCacheDatabase {
	/**
	 * Berkeley DB.
	 */
	private final Database database;

	/**
	 * Constructs the cache.
	 * 
	 * @param database
	 */
	public BerkeleyDBCacheDatabase(final Database database) {
		this.database = database;
	}

	public HttpCacheEntry getEntry(final Transaction txn, final String key)
			throws IOException {
		try {
			final DatabaseEntry data = new DatabaseEntry();
			final OperationStatus status = database.get(txn, new DatabaseEntry(
					key.getBytes()), data, null);
			if (status == OperationStatus.NOTFOUND) {
				return null;
			} else if (status == OperationStatus.SUCCESS) {
				return HttpCacheEntrySerializer.byteArrayToHttpCacheEntry(data
						.getData());
			} else {
				throw new BerkleyDBHttpCacheStorageException(key, status);
			}

		} catch (final DatabaseException e) {
			throw new IOException(e);
		}
	}

	public void putEntry(final Transaction txn, final String key,
			final HttpCacheEntry entry) throws IOException {
		try {
			final DatabaseEntry data = new DatabaseEntry();
			data.setData(HttpCacheEntrySerializer
					.httpCacheEntryToByteArray(entry));
			final OperationStatus status = database.put(txn, new DatabaseEntry(
					key.getBytes()), data);
			if (status != OperationStatus.SUCCESS) {
				throw new BerkleyDBHttpCacheStorageException(key, status);
			}
		} catch (final DatabaseException e) {
			throw new IOException(e);
		}
	}

	public void removeEntry(final Transaction txn, final String key)
			throws IOException {
		try {
			final OperationStatus status = database.delete(txn,
					new DatabaseEntry(key.getBytes()));
			if (status != OperationStatus.SUCCESS
					|| status != OperationStatus.NOTFOUND) {
				throw new BerkleyDBHttpCacheStorageException(key, status);
			}
		} catch (final DatabaseException e) {
			throw new IOException(e);
		}

	}

	public void updateEntry(final Transaction txn, final String key,
			final HttpCacheUpdateCallback callback) throws IOException,
			HttpCacheUpdateException {
		try {
			final DatabaseEntry data = new DatabaseEntry();
			// Made not final as this will get replaced.
			HttpCacheEntry entry;
			final OperationStatus status = database.get(txn, new DatabaseEntry(
					key.getBytes()), data, null);
			if (status == OperationStatus.NOTFOUND) {
				entry = null;
			} else if (status == OperationStatus.SUCCESS) {
				entry = HttpCacheEntrySerializer.byteArrayToHttpCacheEntry(data
						.getData());
			} else {
				throw new BerkleyDBHttpCacheStorageException(key, status);
			}
			entry = callback.update(entry);

			data.setData(HttpCacheEntrySerializer
					.httpCacheEntryToByteArray(entry));
			final OperationStatus putStatus = database.put(txn,
					new DatabaseEntry(key.getBytes()), data);
			if (putStatus != OperationStatus.SUCCESS) {
				throw new BerkleyDBHttpCacheStorageException(key, putStatus);
			}
		} catch (final DatabaseException e) {
			throw new IOException(e);
		}
	}
}
