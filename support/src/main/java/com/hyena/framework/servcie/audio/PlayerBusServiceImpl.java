/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.audio;

import com.hyena.framework.audio.MediaService;
import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.bus.IBusServiceStatusListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月13日 上午11:25:59
 * 播放服务实现类
 */
public class PlayerBusServiceImpl implements PlayerBusService, IBusServiceStatusListener {

	private static final String TAG = "PlayerBusServiceImpl";
	private static final boolean DEBUG = true;
	
	private Messenger mServiceMessenger;
	private PlayerBusServiceObserver mPlayerBusServiceObserver;
	
	public PlayerBusServiceImpl(Context context){
		mPlayerBusServiceObserver = new PlayerBusServiceObserver();
	}
	
	@Override
	public void play(Song song) throws RemoteException {
		Message msg = new Message();
		msg.what = MediaService.CMD_PLAY;
		Bundle bundle = new Bundle();
		bundle.putSerializable("song", song);
		msg.setData(bundle);
		mServiceMessenger.send(msg);
	}

	@Override
	public void resume() throws Exception {
		Message msg = new Message();
		msg.what = MediaService.CMD_RESUME;
		mServiceMessenger.send(msg);
	}

	@Override
	public void pause() throws RemoteException {
		Message msg = new Message();
		msg.what = MediaService.CMD_PAUSE;
		mServiceMessenger.send(msg);
	}

	@Override
	public void seekTo(long position) throws Exception {
		Message msg = new Message();
		msg.what = MediaService.CMD_SEEK;
		msg.arg1 = (int) position;
		mServiceMessenger.send(msg);
	}

	@Override
	public void getPosition() throws Exception {
		Message msg = new Message();
		msg.what = MediaService.CMD_REQUEST_POSITION;
		msg.replyTo = new Messenger(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				LogUtil.v(TAG, "msg_play_progress : " + msg.arg1);
				long progress = msg.arg1;
				long duration = msg.arg2;
				getPlayerBusServiceObserver().notifyPlayProgressChange(progress, duration);
			}
		});
		mServiceMessenger.send(msg);
	}

	@Override
	public PlayerBusServiceObserver getPlayerBusServiceObserver() {
		return mPlayerBusServiceObserver;
	}
	
	@Override
	public void onReceiveServiceAction(int type, Song song, Intent intent) {
		switch (type) {
//		case MediaService.MSG_REFRESH_DOWNLOAD_PROGRESS://刷新下载进度
//		{
//			LogUtil.v(TAG, "msg_loading_progress : " + intent.getIntExtra("load_progress", -1));
//			int percent = intent.getIntExtra("load_progress", -1);
//			long duration = intent.getLongExtra("duration", -1);
//			getPlayerBusServiceObserver().notifyDownloadProgressChange(percent, duration);
//			break;
//		}
//		case MediaService.MSG_REFRESH_PLAY_PROGRESS://刷新播放进度
//		{
//			LogUtil.v(TAG, "msg_play_progress : " + intent.getLongExtra("play_progress", -1));
//			long progress = intent.getLongExtra("play_progress", -1);
//			long duration = intent.getLongExtra("duration", -1);
//			getPlayerBusServiceObserver().notifyPlayProgressChange(progress, duration);
//			break;
//		}
		case MediaService.MSG_REFRESH_PLAYSTATUS_CHANGE://播放状态改变
		{
			int status = intent.getIntExtra("status", StatusCode.STATUS_UNINITED);
			if(DEBUG)
				LogUtil.v(TAG, "msg_play_status_change : " + StatusCode.getStatusLabel(status));
			getPlayerBusServiceObserver().notifyPlayStatusChange(song, status);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onServiceConnected(Messenger messenger) {
		this.mServiceMessenger = messenger;
	}

	@Override
	public void onServiceDisConnected() {
		this.mServiceMessenger = null;
	}

	@Override
	public void releaseAll() {

	}
}
