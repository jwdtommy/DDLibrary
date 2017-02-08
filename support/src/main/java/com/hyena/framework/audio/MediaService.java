/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.audio.player.BasePlayer.OnPlayStateChangeListener;
import com.hyena.framework.clientlog.LogUtil;

/**
 * 媒体播放服务
 * @author yangzc
 */
public class MediaService extends Service {

	private static final String TAG = "MediaService";
	private static final boolean DEBUG = true;
	
	public static final int CMD_PLAY = 0;//播放歌曲
	public static final int CMD_RESUME = 1;//还原播放
	public static final int CMD_PAUSE = 2;//暂停
	public static final int CMD_SEEK = 3;//seekTo
	public static final int CMD_REQUEST_POSITION = 4;//获得播放位置
	
	//回调消息
	private static final int MSG_REFRESH_START_CODE = 100;
//	public static final int MSG_REFRESH_PLAY_PROGRESS = MSG_REFRESH_START_CODE +1;//刷新播放进度
//	public static final int MSG_REFRESH_DOWNLOAD_PROGRESS = MSG_REFRESH_START_CODE +2;//刷新加载进度
	public static final int MSG_REFRESH_PLAYSTATUS_CHANGE = MSG_REFRESH_START_CODE + 3;//播放状态改变
	
	//播放服务事件
	public static final String SERVICE_PLAY_EVENT_ACTION = "com.baidu.music.player_service_evt";
	
	//播放器
	private MusicPlayer mMusicPlayer;
	private Messenger mClientMessenger;
	private HandlerThread mIoHandlerThread = null;
	
	private MediaServiceHelper mPlayServiceHelper;//播放相关帮助类
	
	@Override
	public IBinder onBind(Intent intent) {
		if(mClientMessenger == null){
			mClientMessenger = new Messenger(new Handler(mIoHandlerThread.getLooper()){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					handleClientMessageImpl(msg);
				}
			});
		}
		return mClientMessenger.getBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mIoHandlerThread = new HandlerThread("mediaplayer");
		mIoHandlerThread.start();
		//初始化
		mPlayServiceHelper = new MediaServiceHelper();
		
		//初始化音乐播放器
		mMusicPlayer = new MusicPlayer(mIoHandlerThread.getLooper());
		mMusicPlayer.setOnPlayStateChangeListener(mPlayStateChangeListener);
//		mMusicPlayer.setOnPlayPositionChangeListener(mPlayPositionChangeListener);
	}
	
	private OnPlayStateChangeListener mPlayStateChangeListener = new OnPlayStateChangeListener() {
		
		@Override
		public void onPlayStateChange(int state) {
			if(DEBUG)
				LogUtil.v(TAG,  "player status: " + StatusCode.getStatusLabel(state));
			
			//通知播放信息改变
			if(mPlayServiceHelper != null)
				mPlayServiceHelper.notifyPlayStatusChange(mMusicPlayer.getCurrentSong(), state);
		}
	};

//	private BasePlayer.OnPlayPositionChangeListener mPlayPositionChangeListener = new BasePlayer.OnPlayPositionChangeListener() {
//		@Override
//		public void onPositionChange(long position, long duration) {
			//通知播放进度
//			if(mPlayServiceHelper != null) {
//				mPlayServiceHelper.notifyPlayProgressChange(mMusicPlayer.getCurrentSong(), position, duration);
//			}
//		}
//	};
	
	/**
	 * 处理客户端请求
	 * @param msg
	 */
	private void handleClientMessageImpl(Message msg) {
		int what = msg.what;
		if(DEBUG){
			LogUtil.v(TAG,  "player cmd: " + what);
		}
		switch (what) {
		case CMD_PLAY://播放歌曲
		{
			msg.getData().setClassLoader(getClassLoader());
			Song song = (Song) msg.getData().getSerializable("song");
			playImpl(song);
			break;
		}
		case CMD_RESUME://还原播放
		{
			resumeImpl();
			break;
		}
		case CMD_PAUSE://暂停
		{
			pauseImpl();
			break;
		}
		case CMD_SEEK://seekTo
		{
			if (mMusicPlayer != null) {
				try {
					mMusicPlayer.seekTo(msg.arg1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		case CMD_REQUEST_POSITION:
		{
			try {
				Message response = new Message();
				response.arg1 = (int) mMusicPlayer.getPosition();
				response.arg2 = (int) mMusicPlayer.getDuration();
				msg.replyTo.send(response);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
		default:
			break;
		}
	}
	
	/**
	 * 暂停播放
	 */
	private void pauseImpl(){
		if(mMusicPlayer != null){
			mMusicPlayer.pause();
		}	
	}
	
	/**
	 * 恢复播放
	 */
	private void resumeImpl(){
		if(mMusicPlayer != null){
			mMusicPlayer.resume();
		}
	}
	
	/**
	 * 开始播放歌曲
	 * @param song 歌曲
	 */
	private void playImpl(Song song){
		if(mMusicPlayer != null){
			mMusicPlayer.playSong(song);
		}
	}
}
