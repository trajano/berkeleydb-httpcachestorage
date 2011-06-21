package net.trajano.berkeleydbhttpcachestorage;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;

import com.sleepycat.je.Database;

/**
 * Provides a Berkeley DB backed cache storage for
 * {@link org.apache.http.impl.client.cache.CachingHttpClient}.
 * 
 * @author Archimedes Trajano final EnvironmentConfig environmentConfig = new
 *         EnvironmentConfig(); environmentConfig.setAllowCreate(true);
 *         environmentConfig.setTransactional(true); final Environment env = new
 *         Environment(cachePath, environmentConfig); final DatabaseConfig
 *         databaseConfig = new DatabaseConfig();
 *         databaseConfig.setAllowCreate(true);
 *         databaseConfig.setTransactional(true); final Database db =
 *         env.openDatabase(null, "cache", databaseConfig); *
 */
public class BerkeleyDBCachingHttpClient extends CachingHttpClient {
	public BerkeleyDBCachingHttpClient(final Database database) {
		super(new DefaultHttpClient(),
				new BerkeleyDBHttpCacheStorage(database), new CacheConfig());
	}

	public BerkeleyDBCachingHttpClient(final HttpClient client,
			final Database database) {
		super(client, new BerkeleyDBHttpCacheStorage(database),
				new CacheConfig());
	}

	public BerkeleyDBCachingHttpClient(final HttpClient client,
			final Database database, final CacheConfig cacheConfig) {
		super(client, new BerkeleyDBHttpCacheStorage(database), cacheConfig);
	}
}
