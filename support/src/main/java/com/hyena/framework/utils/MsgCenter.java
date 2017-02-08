package com.hyena.framework.utils;

import com.hyena.framework.clientlog.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 消息中心
 * @author yangzc
 */
public class MsgCenter {

	private static final String TAG = "MsgCenter";
	private static final boolean DEBUG = false;
	
	/**
	 * 发送全局广播
	 * @param intent
	 */
	public static void sendGlobalBroadcast(Intent intent){
		try {
			if(intent == null)
				return;
			
			if(DEBUG)
				LogUtil.v(TAG, "sendGlobalBroadcast, action: " + intent.getAction());
			BaseApp.getAppContext().sendBroadcast(intent);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
	
	/**
	 * 顺序广播
	 * @param intent
	 */
	public static void sendGlobalOrderedBroadcast(Intent intent){
		try {
			if(intent == null)
				return;
			
			if(DEBUG)
				LogUtil.v(TAG, "sendGlobalBroadcast, action: " + intent.getAction());
			BaseApp.getAppContext().sendOrderedBroadcast(intent, null);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
	
	/**
	 * 接收全局广播
	 * @param receiver
	 * @param filter
	 */
	public static void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter filter){
		try {
			BaseApp.getAppContext().registerReceiver(receiver, filter);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
	
	/**
	 * 解注册全局广播
	 * @param receiver
	 */
	public static void unRegisterGlobalReceiver(BroadcastReceiver receiver){
		try {
			BaseApp.getAppContext().unregisterReceiver(receiver);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
	
	/**
	 * 发送本地广播
	 * @param intent
	 */
	public static void sendLocalBroadcast(Intent intent){
		try {
			if(DEBUG)
				LogUtil.v(TAG, "sendLocalBroadcast, action: " + intent.getAction());
			LocalBroadcastManager.getInstance(BaseApp.getAppContext()).sendBroadcast(intent);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
	
	/**
	 * 接收本地广播
	 * @param receiver
	 * @param filter
	 */
	public static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter){
		try {
			LocalBroadcastManager.getInstance(BaseApp.getAppContext()).registerReceiver(receiver, filter);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
	
	/**
	 * 解注册本地广播
	 * @param receiver
	 */
	public static void unRegisterLocalReceiver(BroadcastReceiver receiver){
		try {
			LocalBroadcastManager.getInstance(BaseApp.getAppContext()).unregisterReceiver(receiver);
		} catch (Throwable e) {
			LogUtil.e(TAG, e);
		}
	}
}
