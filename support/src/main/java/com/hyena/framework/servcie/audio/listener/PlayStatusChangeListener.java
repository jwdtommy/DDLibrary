/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.audio.listener;

import com.hyena.framework.audio.bean.Song;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月19日 下午7:44:37
 * 播放状态改变回调
 */
public interface PlayStatusChangeListener {

	public void onStatusChange(Song song, int status);
	
}
