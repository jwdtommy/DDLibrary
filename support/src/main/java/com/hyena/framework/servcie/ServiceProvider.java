/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.servcie;

/**
 * 提供者服务
 * @author yangzc
 *
 */
public class ServiceProvider {

	private static ServiceProvider _instance = null;

	private IServiceManager mServiceManager = null;

	private ServiceProvider() {
	}

	public static ServiceProvider getServiceProvider() {
		if (_instance == null) {
			_instance = new ServiceProvider();
		}
		return _instance;
	}

	/**
	 * 注册服务管理器
	 * @param sensor
	 */
	public void registServiceManager(IServiceManager sensor) {
		this.mServiceManager = sensor;
	}

	/**
	 * 获得服务管理器
	 * @return
	 */
	public IServiceManager getServiceManager() {
		return mServiceManager;
	}
}
