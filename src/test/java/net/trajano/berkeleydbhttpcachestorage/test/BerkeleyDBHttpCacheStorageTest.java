package net.trajano.berkeleydbhttpcachestorage.test;

import java.io.File;
import java.io.IOException;

import net.trajano.berkeleydbhttpcachestorage.BerkeleyDBHttpCacheStorage;

import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class BerkeleyDBHttpCacheStorageTest {
	private final File testDirectory = new File("target/testing/storage");

	@Before
	public void setUp() {
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
}
