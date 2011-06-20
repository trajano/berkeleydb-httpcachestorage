package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.internal.BerkeleyDBCacheDatabase;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import com.sleepycat.je.Database;

/**
 * Provides a Berkeley DB backed cache storage for
 * {@link org.apache.http.impl.client.cache.CachingHttpClient}.
 * 
 * @author Archimedes Trajano
 * 
 */
public class BerkeleyDBHttpCacheStorage implements HttpCacheStorage {
	/**
	 * Berkeley Cache DB.
	 */
	private final BerkeleyDBCacheDatabase database;

	/**
	 * Constructs the cache.
	 * 
	 * @param database
	 *            database. The database must not be transactional.
	 */
	public BerkeleyDBHttpCacheStorage(final Database database) {
		this.database = new BerkeleyDBCacheDatabase(database);
	}

	/**
	 * {@inheritDoc}
	 */
	public HttpCacheEntry getEntry(final String key) throws IOException {
		return database.getEntry(null, key);
	}

	/**
	 * {@inheritDoc}
	 */
	public void putEntry(final String key, final HttpCacheEntry entry)
			throws IOException {
		database.putEntry(null, key, entry);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeEntry(final String key) throws IOException {
		database.removeEntry(null, key);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateEntry(final String key,
			final HttpCacheUpdateCallback callback) throws IOException,
			HttpCacheUpdateException {
		database.updateEntry(null, key, callback);
	}
}
