/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.hyena.framework.datacache.db;

import com.hyena.framework.database.BaseItem;

public class DataCacheItem extends BaseItem {

	/** 缓存标识 */
	private String key;
	/** 对象进入缓存时间 */
	private long enterTime;
	/** 有效时间，以毫秒计 **/
	private long validTime;
	/** 上次使用的时间 */
	private long lastUsedTime;
	private String data;
	
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
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * 获取最近使用时间
	 * @return 最近使用时间
	 */
	public long getLastUsedTime() {
		return lastUsedTime;
	}

	/**
	 * 设置最近使用时间
	 * @param lastUsedTime
	 */
	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}
}
