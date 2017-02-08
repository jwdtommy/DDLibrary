package com.hyena.framework.datacache.cache;

/**
 * <h2>缓存获取失败异常</h2>
 */
@SuppressWarnings("serial")
public class CacheExpiredException extends Exception {

	@Override
	public String getMessage() {
		return "Data expired in cache!..";
	}
}