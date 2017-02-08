/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.audio;

import android.content.Intent;

import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.utils.MsgCenter;

import java.util.List;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月12日 上午10:53:50
 * 
 */
class MediaServiceHelper {

	/**
	 * 通知播放进度改变
	 * @param song
	 * @param progress
	 * @param duration
	 */
//	public void notifyPlayProgressChange(Song song, long progress, long duration){
//		Intent intent = buildCommonMsgIntent(song, MediaService.MSG_REFRESH_PLAY_PROGRESS);
//		intent.putExtra("play_progress", progress);
//		intent.putExtra("duration", duration);
//		MsgCenter.sendGlobalBroadcast(intent);
//	}
	
	/**
	 * 通知加载进度改变
	 * @param song
	 * @param progress
	 * @param duration
	 */
//	public void notifyDownloadProgressChange(Song song, int progress, long duration){
//		Intent intent = buildCommonMsgIntent(song, MediaService.MSG_REFRESH_DOWNLOAD_PROGRESS);
//		intent.putExtra("load_progress", progress);
//		intent.putExtra("duration", duration);
//		MsgCenter.sendGlobalBroadcast(intent);
//	}
	
	/**
	 * 播放状态改变
	 * @param song
	 * @param status StatusCode
	 */
	public void notifyPlayStatusChange(Song song, int status){
		Intent intent = buildCommonMsgIntent(song, MediaService.MSG_REFRESH_PLAYSTATUS_CHANGE);
		intent.putExtra("status", status);
		MsgCenter.sendGlobalBroadcast(intent);
	}
	
	/**
	 * 事件通知
	 * @param song
	 * @param type 数据类型
	 * @param values
	 */
	public void notifyEvt(Song song, int type, List<KeyValuePair> values){
		Intent intent = buildCommonMsgIntent(song, type);
		for(int i=0; i< values.size(); i++){
			KeyValuePair pair = values.get(i);
			intent.putExtra(pair.getKey(), pair.getValue());
		}
		MsgCenter.sendGlobalBroadcast(intent);	
	}
	
	/**
	 * 构造通用Intent
	 * @param song
	 * @param type
	 * @return
	 */
	private Intent buildCommonMsgIntent(Song song, int type){
		Intent intent = new Intent(MediaService.SERVICE_PLAY_EVENT_ACTION);
		intent.setExtrasClassLoader(getClass().getClassLoader());
		intent.putExtra("song", song);
		intent.putExtra("type", type);
		return intent;
	}
}
