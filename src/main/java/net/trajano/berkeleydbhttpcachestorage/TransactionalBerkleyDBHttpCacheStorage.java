package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.internal.BerkeleyDBCacheDatabase;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;

public class TransactionalBerkleyDBHttpCacheStorage implements HttpCacheStorage {
	/**
	 * Berkeley Cache DB.
	 */
	private final BerkeleyDBCacheDatabase database;

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
		this.database = new BerkeleyDBCacheDatabase(database);
	}

	public HttpCacheEntry getEntry(final String key) throws IOException {
		final Transaction txn = environment.beginTransaction(null, null);
		try {
			final HttpCacheEntry entry = database.getEntry(txn, key);
			txn.commit();
			return entry;
		} catch (final IOException e) {
			txn.abort();
			throw e;
		}
	}

	public void putEntry(final String key, final HttpCacheEntry entry)
			throws IOException {
		final Transaction txn = environment.beginTransaction(null, null);
		try {
			database.putEntry(txn, key, entry);
			txn.commit();
		} catch (final IOException e) {
			txn.abort();
			throw e;
		}

	}

	public void removeEntry(final String key) throws IOException {
		final Transaction txn = environment.beginTransaction(null, null);
		try {
			database.removeEntry(txn, key);
			txn.commit();
		} catch (final IOException e) {
			txn.abort();
			throw e;
		}
	}

	public void updateEntry(final String key,
			final HttpCacheUpdateCallback callback) throws IOException,
			HttpCacheUpdateException {
		final Transaction txn = environment.beginTransaction(null, null);
		try {
			database.updateEntry(txn, key, callback);
			txn.commit();
		} catch (final IOException e) {
			txn.abort();
			throw e;
		}
	}
}
