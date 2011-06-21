package net.trajano.berkeleydbhttpcachestorage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * Logs the cache status. Note this not log
 * {@link CacheResponseStatus#CACHE_HIT} as this interceptor will not be called
 * in that scenario.
 * 
 * @author trajano
 * 
 */
public class LogCacheStatusHttpResponseInterceptor implements
		HttpResponseInterceptor {

	private final Level level;
	private final Logger logger;

	public LogCacheStatusHttpResponseInterceptor() {
		this(Logger.getAnonymousLogger(), Level.FINE);
	}

	public LogCacheStatusHttpResponseInterceptor(final Logger logger,
			final Level level) {
		this.logger = logger;
		this.level = level;
	}

	public void process(final HttpResponse response, final HttpContext context)
			throws HttpException, IOException {
		final CacheResponseStatus status = (CacheResponseStatus) context
				.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
		if (status != null) {
			logger.log(level, status.toString());
		}
	}

}
