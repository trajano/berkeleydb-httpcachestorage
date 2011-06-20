package net.trajano.berkeleydbhttpcachestorage.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages.
 * 
 * @author Archimedes Trajano
 * 
 */
public final class Messages {
	/**
	 * Bundle name.
	 */
	private static final String BUNDLE_NAME = "net.trajano.berkeleydbhttpcachestorage.internal.messages"; //$NON-NLS-1$

	/**
	 * Resource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * Gets a string.
	 * 
	 * @param key
	 *            key
	 * @return string in resource bundle.
	 */
	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Prevent instantiation of utility class.
	 */
	private Messages() {
	}
}
