package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;

public class TransactionalBerkleyDBHttpCacheStorage implements HttpCacheStorage {
	/**
	 * Berkeley DB.
	 */
	private final Database database;

	/**
	 * Berkeley DB environment. Used to get the transaction.
	 */
	private final Environment environment;

	/**
	 * Constructs the cache.
	 * 
	 * @param environment
	 *            Berkeley DB environment.
	 * @param database
	 */
	public TransactionalBerkleyDBHttpCacheStorage(
			final Environment environment, final Database database) {
		this.environment = environment;
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
