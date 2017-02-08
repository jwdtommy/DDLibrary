/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.bus;

import com.hyena.framework.audio.bean.Song;

import android.content.Intent;
import android.os.Messenger;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月20日 下午1:37:07
 * 服务状态监听器
 */
public interface IBusServiceStatusListener {

	/**
	 * 收到服务消息
	 * @param type
	 * @param song
	 * @param intent
	 */
	public void onReceiveServiceAction(int type, Song song, Intent intent);
	
	/**
	 * 连接到服务
	 * @param messenger
	 */
	public void onServiceConnected(Messenger messenger);

	/**
	 * 与服务断开连接
	 */
	public void onServiceDisConnected();
}
