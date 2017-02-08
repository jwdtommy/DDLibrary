package com.hyena.framework.datacache.cache;

import com.hyena.framework.datacache.db.DataCacheItem;

/**
 * <h2>缓存条目</h2>
 */
public class CacheEntry {
	/** 被缓存的数据对象 */
	private Cacheable object;
	/** 缓存标识 */
	private String key;
	/** 对象进入缓存时间 */
	private long enterTime;
	/** 有效时间，以毫秒计 **/
	private long validTime;
	/** 上次使用的时间 */
	private long lastUsedTime;

// --Commented out by Inspection START (2014/10/16 9:53):
//	public CacheEntry(Cacheable object, long enterTime, long validTime) {
//		super();
//		this.object = object;
//		this.enterTime = enterTime;
//		this.validTime = validTime;
//	}
// --Commented out by Inspection STOP (2014/10/16 9:53)

	public CacheEntry() {
		// TODO Auto-generated constructor stub
	}

	public CacheEntry(String key, Cacheable obj, long enterTime, long validTime) {
		this.key = key;
		this.object = obj;
		this.enterTime = enterTime;
		this.validTime = validTime;
	}

	public CacheEntry(Cacheable obj) {
		this.object = obj;
	}

	public Cacheable getObject() {
		return object;
	}

	public void setObject(Cacheable object) {
		this.object = object;
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}

	public long getValidTime() {
		return validTime;
	}

	public void setValidTime(long validTime) {
		this.validTime = validTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getData() {
		if (object == null)
			return null;
		return object.buildCacheData();
	}

	public void setData(String data) {
		if (object == null)
			return;
		object = object.parseCacheData(data);
	}

	/**
	 * 获取最近使用时间
	 * 
	 * @return 最近使用时间
	 */
	public long getLastUsedTime() {
		return lastUsedTime;
	}

	/**
	 * 设置最近使用时间
	 * 
	 * @param lastUsedTime
	 */
	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	/**
	 * 是否已过期
	 * 
	 * @return 是否过期
	 */
	public boolean isExpired() {
		return System.currentTimeMillis() > enterTime + validTime;
	}

	/**
	 * 获取当前缓存对象所占内存大小
	 * 
	 * @return 当前缓存对象所占内存大小(以字节计)
	 */
	public long caculateMemSize() {
		if (this.object == null)
			return 0;
		return this.object.calculateMemSize();
	}

	@Override
	public String toString() {
		return "CacheEntry [object=" + object + ", key=" + key + ", enterTime="
				+ enterTime + ", validTime=" + validTime + ", lastUsedTime="
				+ lastUsedTime + "]";
	}
	
	public DataCacheItem toDataCacheItem(){
		DataCacheItem dataCacheItem = new DataCacheItem();
		dataCacheItem.setData(getData());
		dataCacheItem.setEnterTime(getEnterTime());
		dataCacheItem.setKey(getKey());
		dataCacheItem.setLastUsedTime(getLastUsedTime());
		dataCacheItem.setValidTime(getValidTime());
		return dataCacheItem;
	}
}