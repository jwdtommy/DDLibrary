package com.hyena.framework.database;

/**
 * 基础抽象bean
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月29日 下午5:54:38
 *
 */
public abstract class BaseItem {

	protected long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
