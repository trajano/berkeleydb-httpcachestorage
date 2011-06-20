package net.trajano.berkeleydbhttpcachestorage.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.trajano.berkeleydbhttpcachestorage.BerkleyDBHttpCacheStorageClassNotFoundException;

import org.apache.http.client.cache.HttpCacheEntry;

public final class HttpCacheEntrySerializer {
	public static HttpCacheEntry byteArrayToHttpCacheEntry(final byte[] bytes)
			throws IOException {
		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(bytes));
		try {
			return (HttpCacheEntry) objectInputStream.readObject();
		} catch (final ClassNotFoundException e) {
			throw new BerkleyDBHttpCacheStorageClassNotFoundException(e);
		} finally {
			objectInputStream.close();
		}
	}

	public static byte[] httpCacheEntryToByteArray(final HttpCacheEntry entry)
			throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				baos);
		try {
			objectOutputStream.writeObject(entry);
			return baos.toByteArray();
		} finally {
			objectOutputStream.close();
			baos.close();
		}
	}

	/**
	 * Prevent instantiation of utility class.
	 */
	private HttpCacheEntrySerializer() {

	}
}
