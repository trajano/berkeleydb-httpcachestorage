package net.trajano.berkeleydbhttpcachestorage.test;

import java.io.File;
import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.BerkeleyDBHttpCacheStorage;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
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
				.execute(new HttpGet(
						"http://www.gravatar.com/avatar/a798a3d661375ece15776f83fbb80c2c.png"));
		Assert.assertNotNull(response);
		final HttpEntity entity = response.getEntity();
		Assert.assertNotNull(entity);
		EntityUtils.toByteArray(entity);
		return System.nanoTime() - start;
	}

	/**
	 * Creates the test directory.
	 * 
	 * @throws IOException
	 *             I/O error had occurred.
	 */
	@Before
	public void setUp() throws IOException {
		FileUtils.deleteDirectory(testDirectory);
		Assert.assertTrue(testDirectory.mkdirs());
		Assert.assertTrue(testDirectory.exists());
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
				new BerkeleyDBHttpCacheStorage(db), new CacheConfig());
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
				new CacheConfig());

		final long time1 = doRequest(httpClient);
		final long time2 = doRequest(httpClient);
		Assert.assertTrue(time2 < time1);
		db.close();
		env.close();
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

}
