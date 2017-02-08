/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network;

import android.content.Context;
import android.net.ConnectivityManager;

import com.hyena.framework.bean.KeyValuePair;

import java.util.List;

/**
 * 网络感应器
 * @author yangzc
 * 
 * 后续可考虑把网路相应的判断逻辑加到这里来
 */
public interface NetworkSensor {

	/**
	 * 重新构建URl
	 * @param url
	 * @return
	 */
	public String rebuildUrl(String url);

	/**
	 * 获取通用头信息
	 * @return
	 */
	public List<KeyValuePair> getCommonHeaders(String url, boolean isProxy);
	
	/**
	 * 代理服务器地址
	 * @param url 当前请求的URL
	 * @param isProxy 是否走代理
	 * @return
	 */
	public HttpExecutor.ProxyHost getProxyHost(String url, boolean isProxy);
	
	/**
	 * 发生流量
	 * @param len 流量数据
	 */
	public void updateFlowRate(long len);

	/**
	 * 是否有可用网络
	 * @return
	 */
	public boolean isNetworkAvailable();
	
	/**
	 * 获得链接管理器
	 * @param context
	 * @return
	 */
	public ConnectivityManager getConnectivityManager(Context context);
}
