/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network;

public class NetworkProvider {

	private static NetworkProvider _instance = null;
	
	private NetworkSensor mNetworkSensor = new DefaultNetworkSensor();
	private NetworkProvider(){}
	
	public static NetworkProvider getNetworkProvider(){
		if(_instance == null){
			_instance = new NetworkProvider();
		}
		return _instance;
	}
	
	public void registNetworkSensor(NetworkSensor sensor){
		this.mNetworkSensor = sensor;
	}
	
	public NetworkSensor getNetworkSensor(){
		return mNetworkSensor;
	}
}
