/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio.player;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;

/**
 * 媒体播放器
 * @author yangzc
 */
public abstract class BasePlayer {

	private Song mSong;
	
	/**
	 * 设置播放源
	 * @param song
	 */
	public void setDataSource(Song song){
		this.mSong = song;
	}
	
	/**
	 * 获得数据源
	 * @return
	 */
	public Song getDataSource(){
		return mSong;
	}
	
	/**
	 * 开始播放
	 */
	public abstract void play();
	
	/**
	 * 暂停播放
	 */
	public abstract void pause();
	
	/**
	 * 停止播放
	 */
	public abstract void stop();
	
	/**
	 * 重置播放器
	 */
	public void reset(){
		mSong = null;
	}
	
	/**
	 * 释放资源
	 */
	public abstract void release();
	
	/**
	 * 获得播放时长
	 * @return
	 */
	public abstract int getDuration();
	
	/**
	 * 获得当前播放位置
	 * @return
	 */
	public abstract int getCurrentPosition();
	
	/**
	 * seekto
	 * @param position
	 */
	public abstract void seekTo(int position) throws Exception;
	
	//当前播放器状态
	private int mState = StatusCode.STATUS_UNINITED;
	//内部循环looper
	private Handler mLooperHandler;
	
	public BasePlayer(Looper looper){
		//初始化内部looper
		mLooperHandler = new Handler(looper){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				handleMessageImpl(msg);
			}
		};
	}
	
	/**
	 * 获得looperHandle
	 * @return
	 */
	public Handler getLooperHandle(){
		return mLooperHandler;
	}
	
	/**
	 * 收到Looper消息
	 * @param msg
	 */
	public void handleMessageImpl(Message msg) {}
	
	/**
	 * 是否是暂停播放中
	 * @return
	 */
	public boolean isPaused(){
		return mState == StatusCode.STATUS_PAUSE;
	}
	
	/**
	 * 是否正在播放中
	 */
	public boolean isPlaying(){
		return mState == StatusCode.STATUS_PLAYING;
	}
	
	/**
	 * 设置当前状态
	 * @param state
	 */
	public void setState(int state) {
		this.mState = state;
		if(mPlayStateChangeListener != null){
			mPlayStateChangeListener.onPlayStateChange(state);
		}
	}
	
	//播放状态改变回调
	private OnPlayStateChangeListener mPlayStateChangeListener;
	
	/**
	 * 设置播放状态改变监听器
	 * @param listener
	 */
	public void setOnPlayStateChangeListener(OnPlayStateChangeListener listener){
		this.mPlayStateChangeListener = listener;
	}
	
	/**
	 * 通知播放进度改变
	 * @param position
	 */
	public void onPlayPositionChange(int position, int duration){
		if(mPlayPositionChangeListener != null){
			mPlayPositionChangeListener.onPositionChange(position, duration);
		}
	}
	
	//播放进度改变
	private OnPlayPositionChangeListener mPlayPositionChangeListener;
	
	/**
	 * 播放进度改变监听器
	 * @param listener
	 */
	public void setOnPlayPositionChangeListener(OnPlayPositionChangeListener listener){
		this.mPlayPositionChangeListener = listener;
	}
	
	/**
	 * 播放进度改变
	 * @author yangzc
	 */
	public static interface OnPlayPositionChangeListener {
		public void onPositionChange(long position, long duration);
	}
	
	/**
	 * 播放状态改变监听器
	 * @author yangzc
	 */
	public static interface OnPlayStateChangeListener {
		
		/**
		 * 播放状态发生改变
		 * @param state 播放状态
		 */
		public void onPlayStateChange(int state);
	}
}
