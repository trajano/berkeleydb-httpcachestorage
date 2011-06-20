package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import com.sleepycat.je.Database;

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
		// TODO Auto-generated method stub
		return null;
	}

	public void putEntry(final String key, final HttpCacheEntry entry)
			throws IOException {
		// TODO Auto-generated method stub

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
