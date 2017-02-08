/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio.player;

import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.clientlog.LogUtil;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Looper;
import android.os.Message;

/**
 * 默认音乐播放器
 * @author yangzc
 */
public class LocalPlayer extends BasePlayer {

	private static final String TAG = "DefaultPlayer";
	//循环消息
	public static final int MSG_LOOPER = 1;
	//系统播放器
	private MediaPlayer mMediaPlayer;
	//当前播放位置
	private volatile int mCurrentPos = 0;
	
	public LocalPlayer(Looper looper) {
		super(looper);
		setState(StatusCode.STATUS_UNINITED);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// 设置各种listener
		mMediaPlayer.setOnCompletionListener(mCompletionListener);
		mMediaPlayer.setOnErrorListener(mErrorListener);
		//初始化完成
		setState(StatusCode.STATUS_INITED);
		getLooperHandle().sendEmptyMessage(MSG_LOOPER);
	}

	@Override
	public void setDataSource(Song song) {
		reset();
		super.setDataSource(song);
		try {
			mMediaPlayer.setDataSource(song.getLocalFile().getAbsolutePath());
			mMediaPlayer.prepare();
			setState(StatusCode.STATUS_PREPARED);
		} catch (Exception e) {
			e.printStackTrace();
			setState(StatusCode.STATUS_ERROR);
		}
	}

	@Override
	public void play() {
		mMediaPlayer.start();
		setState(StatusCode.STATUS_PLAYING);
	}

	@Override
	public void pause() {
		LogUtil.v(TAG, "pause");
		// 已经暂停或者停止，不可暂停
		if (!isPlaying()) {
			return;
		}
		try {
			mMediaPlayer.pause();
			setState(StatusCode.STATUS_PAUSE);
		} catch (IllegalStateException e) {
			LogUtil.e(TAG, e);
			setState(StatusCode.STATUS_ERROR);
		}
	
	}

	@Override
	public void stop() {
		mMediaPlayer.stop();
		setState(StatusCode.STATUS_STOP);
	}

	@Override
	public void reset() {
		super.reset();
		mMediaPlayer.reset();
		setState(StatusCode.STATUS_INITED);
	}

	@Override
	public void release() {
		mMediaPlayer.release();
		setState(StatusCode.STATUS_RELEASE);
	}

	@Override
	public int getCurrentPosition() {
		return mCurrentPos;
//		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int position) throws Exception {
		if (mMediaPlayer != null) {
			try {
				mMediaPlayer.seekTo(position);
			} catch (IllegalStateException e) {
				LogUtil.e(TAG, e);
				throw e;
			}
		}
	}

	@Override
	public int getDuration() {
		if (isPlaying() || isPaused()) {
			return mMediaPlayer.getDuration();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}
	
	@Override
	public void handleMessageImpl(Message msg) {
		super.handleMessageImpl(msg);
		if(msg.what == MSG_LOOPER){
			if(getDataSource() != null && (isPlaying() || isPaused())) {
				mCurrentPos = mMediaPlayer.getCurrentPosition();
				onPlayPositionChange(mCurrentPos, getDuration());
			}
			getLooperHandle().sendEmptyMessageDelayed(MSG_LOOPER, 1000);
		}
	}

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			setState(StatusCode.STATUS_COMPLETED);
		}
	};
	
	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			setState(StatusCode.STATUS_ERROR);
			return false;
		}
	};

}
