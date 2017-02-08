/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.servcie;

/**
 * 服务管理器
 * @author yangzc
 *
 */
public interface IServiceManager {

	/**
	 * 获得服务
	 * @param name 名称
	 * @return
	 */
	public Object getService(String name);
	
	/**
	 * 释放所有服务
	 */
	public void releaseAll();
	
	/**
	 * 动态注册服务
	 * @param name
	 * @param service
	 */
	public void registService(String name, Object service);
	
	/**
	 * 解注册服务
	 * @param name
	 */
	public void unRegistService(String name);
}
