package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.OperationStatus;

public class BerkleyDBHttpCacheStorage implements HttpCacheStorage {
	/**
	 * Berkeley DB.
	 */
	private final Database database;

	/**
	 * Constructs the cache.
	 * 
	 * @param database
	 */
	public BerkleyDBHttpCacheStorage(final Database database) {
		this.database = database;
	}

	public HttpCacheEntry getEntry(final String key) throws IOException {
		try {
			final DatabaseEntry data = new DatabaseEntry();
			final OperationStatus status = database.get(null,
					new DatabaseEntry(key.getBytes()), data, null);
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

	public void putEntry(final String key, final HttpCacheEntry entry)
			throws IOException {
		try {
			final DatabaseEntry data = new DatabaseEntry();
			data.setData(HttpCacheEntrySerializer
					.HttpCacheEntryToByteArray(entry));
			final OperationStatus status = database.put(null,
					new DatabaseEntry(key.getBytes()), data);
			if (status != OperationStatus.SUCCESS) {
				throw new BerkleyDBHttpCacheStorageException(key, status);
			}
		} catch (final DatabaseException e) {
			throw new IOException(e);
		}
	}

	public void removeEntry(final String key) throws IOException {
		// TODO Auto-generated method stub

	}

	public void updateEntry(final String key,
			final HttpCacheUpdateCallback callback) throws IOException,
			HttpCacheUpdateException {
		// TODO Auto-generated method stub

	}

}
