package net.trajano.berkeleydbhttpcachestorage.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.trajano.berkeleydbhttpcachestorage.BerkeleyDBHttpCacheStorage;
import net.trajano.berkeleydbhttpcachestorage.LogCacheStatusHttpResponseInterceptor;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * Tests {@link BerkeleyDBHttpCacheStorage}.
 * 
 * @author Archimedes Trajano
 * 
 */
public class BerkeleyDBHttpCacheStorageTest {
	/**
	 * Cache configuration;
	 */
	private CacheConfig cacheConfig = null;

	/**
	 * Test directory.
	 */
	private final File testDirectory = new File("target/testing/storage");

	/**
	 * Gets an object and returns the time it took in nanoseconds.
	 * 
	 * @param httpClient
	 *            HTTP Client
	 * @return time it took for the operation to complete in nanoseconds.
	 * @throws IOException
	 *             I/O error had occurred.
	 */
	private long doRequest(final HttpClient httpClient) throws IOException {
		final long start = System.nanoTime();
		final HttpResponse response = httpClient
				.execute(
						new HttpGet(
								"http://www.gravatar.com/avatar/a798a3d661375ece15776f83fbb80c2c.png"),
						new BasicHttpContext());
		Assert.assertNotNull(response);
		final HttpEntity entity = response.getEntity();
		Assert.assertNotNull(entity);
		EntityUtils.toByteArray(entity);
		return System.nanoTime() - start;
	}

	/**
	 * Creates the test directory. Also sets the {@link CacheConfig}.
	 * 
	 * @throws IOException
	 *             I/O error had occurred.
	 */
	@Before
	public void setUp() throws IOException {
		FileUtils.deleteDirectory(testDirectory);
		Assert.assertTrue(testDirectory.mkdirs());
		Assert.assertTrue(testDirectory.exists());

		cacheConfig = new CacheConfig();
		cacheConfig.setHeuristicCachingEnabled(true);
		cacheConfig.setSharedCache(true);
		cacheConfig.setMaxObjectSizeBytes(Integer.MAX_VALUE);

	}

	/**
	 * Removes the test directory.
	 * 
	 * @throws IOException
	 *             I/O error had occurred.
	 */
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(testDirectory);
		Assert.assertFalse(testDirectory.exists());
	}

	/**
	 * Tests the creation of the object.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testCreate() throws Exception {
		final EnvironmentConfig environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		final Environment env = new Environment(testDirectory,
				environmentConfig);
		final DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		final Database db = env.openDatabase(null, "cache", databaseConfig);
		new CachingHttpClient(new DefaultHttpClient(),
				new BerkeleyDBHttpCacheStorage(db), cacheConfig);
		db.close();
		env.close();
	}

	/**
	 * Tests multiple requests, the second request should take less time.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testDoMultipleRequest() throws Exception {
		final EnvironmentConfig environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		final Environment env = new Environment(testDirectory,
				environmentConfig);
		final DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		final Database db = env.openDatabase(null, "cache", databaseConfig);
		final HttpClient httpClient = new CachingHttpClient(
				new DefaultHttpClient(), new BerkeleyDBHttpCacheStorage(db),
				cacheConfig);

		final long time1 = doRequest(httpClient);
		final long time2 = doRequest(httpClient);
		Assert.assertTrue(time2 <= time1);
		db.close();
		env.close();
	}

	/**
	 * Tests multiple requests, the second request should take less time.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testDoMultipleRequestOverTwoByStatus() throws Exception {
		{
			final EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			final Environment env = new Environment(testDirectory,
					environmentConfig);
			final DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			final Database db = env.openDatabase(null, "cache", databaseConfig);
			final HttpClient httpClient = new CachingHttpClient(
					new DefaultHttpClient(),
					new BerkeleyDBHttpCacheStorage(db), cacheConfig);

			final HttpContext localContext = new BasicHttpContext();
			final HttpResponse response = httpClient
					.execute(
							new HttpGet(
									"http://www.gravatar.com/avatar/a798a3d661375ece15776f83fbb80c2c.png"),
							localContext);
			Assert.assertNotNull(response);
			final HttpEntity entity = response.getEntity();
			Assert.assertNotNull(entity);
			EntityUtils.toByteArray(entity);

			final CacheResponseStatus responseStatus = (CacheResponseStatus) localContext
					.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);

			Assert.assertEquals(CacheResponseStatus.CACHE_MISS, responseStatus);

			db.close();
			env.close();
		}
		{
			final EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			final Environment env = new Environment(testDirectory,
					environmentConfig);
			final DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			final Database db = env.openDatabase(null, "cache", databaseConfig);
			final HttpClient httpClient = new CachingHttpClient(
					new DefaultHttpClient(),
					new BerkeleyDBHttpCacheStorage(db), cacheConfig);

			final HttpContext localContext = new BasicHttpContext();
			final HttpResponse response = httpClient
					.execute(
							new HttpGet(
									"http://www.gravatar.com/avatar/a798a3d661375ece15776f83fbb80c2c.png"),
							localContext);
			Assert.assertNotNull(response);
			final HttpEntity entity = response.getEntity();
			Assert.assertNotNull(entity);
			EntityUtils.toByteArray(entity);

			final CacheResponseStatus responseStatus = (CacheResponseStatus) localContext
					.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);

			Assert.assertEquals(CacheResponseStatus.CACHE_HIT, responseStatus);
			db.close();
			env.close();
		}
	}

	/**
	 * Tests multiple requests, the second request should take less time.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testDoMultipleRequestOverTwoByTime() throws Exception {
		final long time1;
		{
			final EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			final Environment env = new Environment(testDirectory,
					environmentConfig);
			final DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			final Database db = env.openDatabase(null, "cache", databaseConfig);
			final HttpClient httpClient = new CachingHttpClient(
					new DefaultHttpClient(),
					new BerkeleyDBHttpCacheStorage(db), cacheConfig);

			time1 = doRequest(httpClient);
			db.close();
			env.close();
		}
		final long time2;
		{
			final EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			final Environment env = new Environment(testDirectory,
					environmentConfig);
			final DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			final Database db = env.openDatabase(null, "cache", databaseConfig);
			final HttpClient httpClient = new CachingHttpClient(
					new DefaultHttpClient(),
					new BerkeleyDBHttpCacheStorage(db), cacheConfig);

			time2 = doRequest(httpClient);
			db.close();
			env.close();
		}
		Assert.assertTrue(time2 <= time1);
	}

	/**
	 * Tests multiple requests, the second request should take less time.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testDoMultipleRequestOverTwoWithLogging() throws Exception {
		final Logger logger = Logger.getLogger(this.getClass().getName());
		final long time1;
		{
			final EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			final Environment env = new Environment(testDirectory,
					environmentConfig);
			final DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			final Database db = env.openDatabase(null, "cache", databaseConfig);
			final DefaultHttpClient client = new DefaultHttpClient();
			final HttpResponseInterceptor interceptor = new LogCacheStatusHttpResponseInterceptor(
					logger, Level.INFO);
			client.addResponseInterceptor(interceptor);
			final HttpClient httpClient = new CachingHttpClient(client,
					new BerkeleyDBHttpCacheStorage(db), cacheConfig);

			time1 = doRequest(httpClient);
			db.close();
			env.close();
		}
		final long time2;
		{
			final EnvironmentConfig environmentConfig = new EnvironmentConfig();
			environmentConfig.setAllowCreate(true);
			final Environment env = new Environment(testDirectory,
					environmentConfig);
			final DatabaseConfig databaseConfig = new DatabaseConfig();
			databaseConfig.setAllowCreate(true);
			final Database db = env.openDatabase(null, "cache", databaseConfig);
			final DefaultHttpClient client = new DefaultHttpClient();
			final HttpResponseInterceptor interceptor = new LogCacheStatusHttpResponseInterceptor(
					logger, Level.INFO);
			client.addResponseInterceptor(interceptor);
			final HttpClient httpClient = new CachingHttpClient(client,
					new BerkeleyDBHttpCacheStorage(db), cacheConfig);

			time2 = doRequest(httpClient);
			db.close();
			env.close();
		}
		Assert.assertTrue(time2 <= time1);
	}

	/**
	 * Tests a single request.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testDoRequest() throws Exception {
		final EnvironmentConfig environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		final Environment env = new Environment(testDirectory,
				environmentConfig);
		final DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		final Database db = env.openDatabase(null, "cache", databaseConfig);
		final HttpClient httpClient = new CachingHttpClient(
				new DefaultHttpClient(), new BerkeleyDBHttpCacheStorage(db),
				new CacheConfig());

		final HttpResponse response = httpClient.execute(new HttpGet(
				"http://slashdot.org"));

		Assert.assertNotNull(response);
		final HttpEntity entity = response.getEntity();
		Assert.assertNotNull(entity);
		EntityUtils.toByteArray(entity);
		db.close();
		env.close();
	}

	/**
	 * Tests using a transactional {@link Database}. Should still work.
	 * 
	 * @throws Exception
	 *             error had occurred.
	 */
	@Test
	public void testTransactional() throws Exception {
		final EnvironmentConfig environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		environmentConfig.setTransactional(true);
		final Environment env = new Environment(testDirectory,
				environmentConfig);
		final DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		final Database db = env.openDatabase(null, "cache", databaseConfig);
		final HttpClient httpClient = new CachingHttpClient(
				new DefaultHttpClient(), new BerkeleyDBHttpCacheStorage(db),
				new CacheConfig());

		final HttpResponse response = httpClient.execute(new HttpGet(
				"http://slashdot.org"));

		Assert.assertNotNull(response);
		final HttpEntity entity = response.getEntity();
		Assert.assertNotNull(entity);
		EntityUtils.toByteArray(entity);
		db.close();
		env.close();
	}

}
