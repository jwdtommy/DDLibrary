/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.audio.player.BasePlayer;
import com.hyena.framework.audio.player.LocalPlayer;
import com.hyena.framework.audio.player.BasePlayer.OnPlayPositionChangeListener;
import com.hyena.framework.audio.player.BasePlayer.OnPlayStateChangeListener;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.CancelableListener;
import com.hyena.framework.utils.BaseFileUtils;

import android.os.Looper;

/**
 * 音乐播放器
 * @author yangzc
 */
public class MusicPlayer {

	private static final String TAG = "MusicPlayer";
	private static boolean DEBUG = true;
	
	//blocksize
	private static int BLOCK_SIZE = 20 * 1024 * 128;
	
	//当前使用的播放器
	private BasePlayer mPlayer;
	//默认播放器 可播放系统支持的音频格式 ex:本地mp3
	private LocalPlayer mLocalPlayer;
	private Song mSong;
	private BufferingThread mBufferingThread;
	private int mPlayState;
	
	public MusicPlayer(Looper looper){
		//初始内置播放器
		mLocalPlayer = new LocalPlayer(looper);
	}
	
	/**
	 * 播放歌曲
	 * @param song
	 */
	public void playSong(Song song){
		this.mSong = song;
		//重置播放器
		resetPlayer();
		//根据音频格式选择播放器
		mPlayer = getPlayer(song);
		
		mPlayer.setOnPlayStateChangeListener(mInnerPlayStateChangeListener);
		mPlayer.setOnPlayPositionChangeListener(mInnerPositionChangeListener);
		if(mBufferingThread != null){
			mBufferingThread.cancel();
		}
		
		if(mSong.isOnline()){
			mBufferingThread = new BufferingThread(song);
			mBufferingThread.start();
		}else{
			mPlayer.setDataSource(mSong);
			mPlayer.play();
		}
		
		//一刀切 默认直接变为buffering
		mPlayState = StatusCode.STATUS_BUFFING;
		if(mInnerPlayStateChangeListener != null){
			mInnerPlayStateChangeListener.onPlayStateChange(mPlayState);
		}
	}
	
	/**
	 * 获得播放器
	 * @param song
	 * @return
	 */
	private BasePlayer getPlayer(Song song){
		return mLocalPlayer;
	}
	
	/**
	 * 暂停播放
	 */
	public void pause(){
		mPlayer.pause();
	}
	
	/**
	 * 还原播放
	 */
	public void resume(){
		mPlayer.play();
	}

	/**
	 * seekTo
	 * @param position
	 */
	public void seekTo(int position) throws Exception{
		mPlayer.seekTo(position);
	}
	
	/**
	 * 获得当前正在播放的歌曲
	 * @return
	 */
	public Song getCurrentSong(){
		return mSong;
	}

	/**
	 * 重置播放器
	 */
	private void resetPlayer(){
		if(mPlayer != null){
			mPlayer.setOnPlayStateChangeListener(null);
			mPlayer.setOnPlayPositionChangeListener(null);
		}
	}
	
	/**
	 * 缓存线程
	 */
	private class BufferingThread extends Thread {
		
		private Song mSong;
		//当前缓冲到得位置
		private long mCurrentPos = 0;
		//文件总长度
		private long mContentLen = 0;
		
		public BufferingThread(Song song) {
			this.mSong = song;
		}
		
		public void cancel(){
			mCancelableListener.cancel();
		}
		
		public long getDownloadedPos(){
			return mCurrentPos;
		}
		
		public long getContentLength(){
			return mContentLen;
		}
		
		@Override
		public void run() {
			super.run();
			mCurrentPos = 0;
			mContentLen = 0;
//			if(mSong.getLocalFile() != null && mSong.getLocalFile().exists()){
//				mCurrentPos = mSong.getLocalFile().length();
//			}
			HttpProvider httpProvider = new HttpProvider();
			HttpResult result = httpProvider.doGet(mSong.getUrl(), 10, mCurrentPos, mCancelableListener);
			if(!result.isSuccess()){
				mInnerPlayStateChangeListener.onPlayStateChange(StatusCode.STATUS_ERROR);
			}
		}
		
		//数据获取监听器
		private CancelableListener mCancelableListener = new CancelableListener() {
			//随机访问流
			private RandomAccessFile mRandomStream;
			
			@Override
			public boolean onStart(long startPos, long contentLength) {
				mCurrentPos = startPos;
				mContentLen = contentLength;
				try {
					if(mSong.getLocalFile().exists())
						mSong.getLocalFile().delete();
					BaseFileUtils
							.createEmptyFile(mSong.getLocalFile().getAbsolutePath(), contentLength);
					mRandomStream = new RandomAccessFile(mSong.getLocalFile(), "rw");
					mRandomStream.seek(startPos);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				mIsFirst = true;
				return true;
			}

			@Override
			public boolean onAdvance(byte[] buffer, int offset, int len) {
				if(mRandomStream != null){
					try {
						mRandomStream.write(buffer, offset, len);
						mCurrentPos += len;
						//检查是否应该还原
						checkBuffering();
//						if(DEBUG)
//							LogUtil.v(TAG, "currentPos: " + mCurrentPos);
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
				return true;
			}
			
			@Override
			public boolean onCompleted() {
				return true;
			}

			@Override
			public void onError(int statusCode) {
			}

			@Override
			public boolean onReady(String url) {
				return true;
			}

			@Override
			public boolean onRelease() {
				try {
					if(mRandomStream != null)
						mRandomStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
			

		};
	}
	
	//播放状态改变回调
	private OnPlayStateChangeListener mPlayStateChangeListener;
	private OnPlayPositionChangeListener mPlayPositionChangeListener;
	
	/**
	 * 设置播放状态改变监听器
	 * @param listener
	 */
	public void setOnPlayStateChangeListener(OnPlayStateChangeListener listener){
		this.mPlayStateChangeListener = listener;
	}

	public void setOnPlayPositionChangeListener(OnPlayPositionChangeListener listener) {
		this.mPlayPositionChangeListener = listener;
	}
	
	private long mPlayPosition;
	private long mDuration;

	public long getPosition(){
		return mPlayPosition;
	}

	public long getDuration(){
		return mDuration;
	}

	private boolean mIsFirst = true;
	/**
	 * 检查是否需要resume
	 */
	private void checkBuffering() {
		if(mPlayState == StatusCode.STATUS_BUFFING) {
			//下载完成
			if(mBufferingThread.getContentLength() == mBufferingThread.getDownloadedPos()){
				//resume
				if(mIsFirst) {
					//设置播放源
					mPlayer.setDataSource(mSong);
					mIsFirst = false;
				}
				if(DEBUG)
					LogUtil.v(TAG, "finish downloaded");
				mPlayer.play();
			}
			else{
				long playPosition = 0;
				if(mDuration > 0)
					playPosition = mPlayPosition * mBufferingThread.getContentLength() / mDuration;
				
				if(mBufferingThread.getDownloadedPos() - playPosition >= BLOCK_SIZE){
					//第一次准备就绪
					if(mIsFirst) {
						//设置播放源
						mPlayer.setDataSource(mSong);
						mIsFirst = false;
					}
					if(DEBUG)
						LogUtil.v(TAG, "first start play");
					mPlayer.play();
				}
			}
		}
	}
	
	//播放进度改变
	private OnPlayPositionChangeListener mInnerPositionChangeListener = new OnPlayPositionChangeListener() {
		
		@Override
		public void onPositionChange(long position, long duration) {
			mPlayPosition = position;
			mDuration = duration;

			if (mPlayPositionChangeListener != null) {
				mPlayPositionChangeListener.onPositionChange(position, duration);
			}
			
			if(mBufferingThread == null)
				return;
			if(DEBUG)
				LogUtil.v(TAG, "buffered: " + mBufferingThread.getDownloadedPos() + ", total: " + mBufferingThread.getContentLength());

			if(DEBUG)
				LogUtil.v(TAG, "position: " + position + ", duration: " + duration);
			
			//下载完成
			if(mBufferingThread.getContentLength() == mBufferingThread.getDownloadedPos()){
				return;
			}
			//没下载完成
			long playPosition = position * mBufferingThread.getContentLength() / duration;
			if(mBufferingThread.getDownloadedPos() - playPosition < BLOCK_SIZE){
				if(DEBUG)
					LogUtil.v(TAG, "onBuffering");
				//buffering
				mPlayer.pause();
				mPlayState = StatusCode.STATUS_BUFFING;
				if(mInnerPlayStateChangeListener != null){
					mInnerPlayStateChangeListener.onPlayStateChange(StatusCode.STATUS_BUFFING);
				}
			}
		}
	};
	
	//内部播放状态改变监听器
	private OnPlayStateChangeListener mInnerPlayStateChangeListener = new OnPlayStateChangeListener() {
		
		@Override
		public void onPlayStateChange(int state) {
			if(DEBUG)
				LogUtil.v(TAG, "onPlayStateChange state: " + StatusCode.getStatusLabel(state));
			mPlayState = state;
			if(mPlayStateChangeListener != null){
				mPlayStateChangeListener.onPlayStateChange(state);
			}
		}
	};
}
