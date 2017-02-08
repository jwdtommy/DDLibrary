/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.audio;

import java.util.ArrayList;
import java.util.List;

import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.servcie.audio.listener.PlayStatusChangeListener;
import com.hyena.framework.servcie.audio.listener.ProgressChangeListener;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月19日 下午4:21:33
 * 
 */
public class PlayerBusServiceObserver {
	
	//-------------------------- 播放状态改变 Start-----------------------------------------
	private List<PlayStatusChangeListener> mPlayStatusChangeListeners;
	public void addPlayStatusChangeListener(PlayStatusChangeListener listener){
		if(mPlayStatusChangeListeners == null)
			mPlayStatusChangeListeners = new ArrayList<PlayStatusChangeListener>();
		if(!mPlayStatusChangeListeners.contains(listener))
			mPlayStatusChangeListeners.add(listener);
	}
	
	public void removemPlayStatusChangeListener(PlayStatusChangeListener listener){
		if(mPlayStatusChangeListeners == null)
			return;
		mPlayStatusChangeListeners.remove(listener);
	}
	
	public void notifyPlayStatusChange(Song song, int status){
		if(mPlayStatusChangeListeners == null)
			return;
		for(PlayStatusChangeListener listener : mPlayStatusChangeListeners){
			listener.onStatusChange(song, status);
		}
	}
	//-------------------------- 播放状态改变  End-----------------------------------------
	
	//-------------------------- 播放进度 Start-----------------------------------------
	private List<ProgressChangeListener> mProgressChangeListeners;
	public void addProgressChangeListener(ProgressChangeListener listener){
		if(mProgressChangeListeners == null)
			mProgressChangeListeners = new ArrayList<ProgressChangeListener>();
		if(!mProgressChangeListeners.contains(listener))
			mProgressChangeListeners.add(listener);
	}
	
	public void removeProgressChangeListener(ProgressChangeListener listener){
		if(mProgressChangeListeners == null)
			return;
		mProgressChangeListeners.remove(listener);
	}
	
	public void notifyPlayProgressChange(long progress, long duration){
		if(mProgressChangeListeners == null)
			return;
		for(ProgressChangeListener listener : mProgressChangeListeners){
			listener.onPlayProgressChange(progress, duration);
		}
	}
	
	public void notifyDownloadProgressChange(int percent, long duration){
		if(mProgressChangeListeners == null)
			return;
		for(ProgressChangeListener listener : mProgressChangeListeners){
			listener.onDownloadProgressChange(percent, duration);
		}
	}
	//-------------------------- 播放进度 End-----------------------------------------
}
