package com.hyena.framework.datacache.cache;

/**
 * <h2>缓存过期异常</h2>
 */
@SuppressWarnings("serial")
public class CacheUncachedException extends Exception {

	@Override
	public String getMessage() {
		return "Data not cached!..";
	}
}