/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.audio.listener;


/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月19日 下午5:12:11
 * 播放进度改变
 */
public interface ProgressChangeListener {

	/**
	 * 播放进度改变
	 * @param progress
	 * @param duration
	 */
	public void onPlayProgressChange(long progress, long duration);
	
	/**
	 * 加载进度改变
	 * @param percent
	 * @param duration
	 */
	public void onDownloadProgressChange(int percent, long duration);
}
