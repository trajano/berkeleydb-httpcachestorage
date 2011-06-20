package net.trajano.berkeleydbhttpcachestorage.test;

import java.io.File;
import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.BerkeleyDBHttpCacheStorage;
import net.trajano.berkeleydbhttpcachestorage.TransactionalBerkeleyDBHttpCacheStorage;

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

public class TransactionalBerkeleyDBHttpCacheStorageTest {
	private final File testDirectory = new File("target/testing/storage");

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

	@Before
	public void setUp() throws IOException {
		FileUtils.deleteDirectory(testDirectory);
		Assert.assertTrue(testDirectory.mkdirs());
		Assert.assertTrue(testDirectory.exists());
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(testDirectory);
		Assert.assertFalse(testDirectory.exists());
	}

	@Test
	public void testCreate() throws Exception {
		final EnvironmentConfig environmentConfig = new EnvironmentConfig();
		environmentConfig.setAllowCreate(true);
		environmentConfig.setTransactional(true);
		final Environment env = new Environment(testDirectory,
				environmentConfig);
		final DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		final Database db = env.openDatabase(null, "cache", databaseConfig);
		new CachingHttpClient(new DefaultHttpClient(),
				new TransactionalBerkeleyDBHttpCacheStorage(env, db),
				new CacheConfig());
		db.close();
		env.close();
	}

	@Test
	public void testDoMultipleRequest() throws Exception {
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
				new DefaultHttpClient(),
				new TransactionalBerkeleyDBHttpCacheStorage(env, db),
				new CacheConfig());

		final long time1 = doRequest(httpClient);
		final long time2 = doRequest(httpClient);
		Assert.assertTrue(time2 < time1);
		db.close();
		env.close();
	}

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
