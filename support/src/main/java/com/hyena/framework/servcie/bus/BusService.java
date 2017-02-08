/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.bus;

import com.hyena.framework.servcie.BaseService;

import android.os.Messenger;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月20日 下午12:18:31
 * 
 */
public interface BusService extends BaseService {

	public static final String BUS_SERVICE_NAME = "service_bus";
	
	/**
	 * 初始化服务
	 */
	public void initService();
	
	/**
	 * 获得远程消息
	 * @return
	 */
	public Messenger getRemoteMessenger();
	
	/**
	 * 添加服务状态监听器
	 * @param listener
	 */
	public void addBusServiceAction(IBusServiceStatusListener listener);
	
	/**
	 * 删除服务状态监听器
	 * @param listener
	 */
	public void removeBusServiceAction(IBusServiceStatusListener listener);
	
}
