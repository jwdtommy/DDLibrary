package com.hyena.framework.datacache.cache;

/**
 * <h2>可缓存对象接口</h2><br/>
 * 被缓存的数据对象需要实现该接口，以提供缓存数据及缓存数据对象所占内存大小等
 * 
 */
public interface Cacheable {
	/**
	 * 计算缓存对象所占内存大小
	 * 
	 * @return 缓存对象所占内存空间大小
	 */
	abstract long calculateMemSize();

	/**
	 * 构建要缓存的数据
	 * 
	 * @return 缓存数据
	 */
	abstract String buildCacheData();

	/**
	 * 将缓存数据解析成数据对象
	 * 
	 * @param data
	 *            缓存数据
	 * 
	 */
	abstract Cacheable parseCacheData(String data);

	/**
	 * 判断是否可以缓存
	 * 
	 * @return 是否可以缓存
	 */
	abstract boolean isCacheable();
}