/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.bus;

import java.util.ArrayList;
import java.util.List;

import com.hyena.framework.audio.MediaService;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.MsgCenter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月20日 下午12:19:29
 * 
 */
public class BusServiceImpl implements BusService {

	private static final String TAG = "BusServiceImpl";
	private static final boolean DEBUG = true;
	
	private Messenger mServiceMessenger;
	private Context mContext;
	
	public BusServiceImpl(Context context) {
		this.mContext = context;
		initService();
	}
	
	@Override
	public void initService() {
		bindService();
		//注册广播服务
		IntentFilter filter = new IntentFilter();
		filter.addAction(MediaService.SERVICE_PLAY_EVENT_ACTION);
		MsgCenter.registerGlobalReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public void releaseAll() {
		unBindService();
		//解注册
		MsgCenter.unRegisterGlobalReceiver(mBroadcastReceiver);
	}

	@Override
	public Messenger getRemoteMessenger() {
		return mServiceMessenger;
	}
	
	private void bindService(){
		Intent intent = new Intent(mContext, MediaService.class);
		mContext.startService(intent);
		mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void unBindService(){
		Intent intent = new Intent(mContext, MediaService.class);
		mContext.stopService(intent);
		mContext.unbindService(mServiceConnection);
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			if(DEBUG)
				LogUtil.v(TAG, "onServiceDisconnected");
			mServiceMessenger = null;
			notifyOnServiceDisConnected();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(DEBUG)
				LogUtil.v(TAG, "onServiceConnected");
			mServiceMessenger = new Messenger(service);
			notifyOnServiceConnected();
		}
	};
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			intent.setExtrasClassLoader(getClass().getClassLoader());
			String action = intent.getAction();
			Song song = (Song) intent.getSerializableExtra("song");
			if(MediaService.SERVICE_PLAY_EVENT_ACTION.equals(action)){
				notifyOnReceiveServiceAction(intent.getIntExtra("type", -1), song, intent);
			}
		}
	};

	
	private List<IBusServiceStatusListener> mBusServiceActions;
	
	@Override
	public void addBusServiceAction(IBusServiceStatusListener listener){
		if(mBusServiceActions == null)
			mBusServiceActions = new ArrayList<IBusServiceStatusListener>();
		if(!mBusServiceActions.contains(listener))
			mBusServiceActions.add(listener);
		
		if(getRemoteMessenger() != null){
			listener.onServiceConnected(getRemoteMessenger());
		}
	}
	
	@Override
	public void removeBusServiceAction(IBusServiceStatusListener listener){
		if(mBusServiceActions == null)
			return;
		mBusServiceActions.remove(listener);
	}
	
	public void notifyOnReceiveServiceAction(int type, Song song, Intent intent){
		if(mBusServiceActions == null)
			return;
		for(IBusServiceStatusListener listener : mBusServiceActions){
			listener.onReceiveServiceAction(type, song, intent);
		}
	}
	
	public void notifyOnServiceConnected(){
		if(mBusServiceActions == null)
			return;
		for(IBusServiceStatusListener listener : mBusServiceActions){
			listener.onServiceConnected(getRemoteMessenger());
		}
	}
	
	public void notifyOnServiceDisConnected(){
		if(mBusServiceActions == null)
			return;
		for(IBusServiceStatusListener listener : mBusServiceActions){
			listener.onServiceDisConnected();
		}
	}
}
