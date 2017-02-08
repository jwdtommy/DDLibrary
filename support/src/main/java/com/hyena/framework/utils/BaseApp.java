package com.hyena.framework.utils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.hyena.framework.app.activity.bean.BasicUserInfo;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;

/**
 * 基础全局App
 * @author yangzc
 */
public class BaseApp extends Application {

	//全局应用上下文
	private static Context mAppContext;
	private static Context mAppContextTmp;
	//用户基本信息
	private static BasicUserInfo mBasicUserInfo;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mAppContextTmp = this;
		//如果进程合法
		if (isProcessValid()) {
			//初始化全局Application
			mAppContext = this;
			//初始化Application
			initApp();
		}
	}

	/**
	 * 获得全局上下文
	 * @return
	 */
	public static Context getAppContext() {
		if (mAppContext == null) {
			return mAppContextTmp;
		}
		return mAppContext;
	}
	
	/**
	 * 用户基础信息
	 * @return
	 */
	public static BasicUserInfo getUserInfo(){
		return mBasicUserInfo;
	}
	
	/**
	 * 是否已经登录
	 * @return
	 */
	public static boolean isLogin(){
		return getUserInfo() != null;
	}
	
	/**
	 * 初始化App
	 */
	public void initApp() {
        IntentFilter globalFilter = new IntentFilter();
        globalFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        MsgCenter.registerGlobalReceiver(mBroadcastReceiver, globalFilter);
    }
	
	/**
	 * 退出App
	 */
	public void exitApp() {
        MsgCenter.unRegisterGlobalReceiver(mBroadcastReceiver);
    }
	
	/**
	 * 全局登录回调 </p>
	 * @param activity 关联的Activity
	 * @param userInfo
	 */
	public void onlogin(Activity activity, BasicUserInfo userInfo) {
		BaseApp.mBasicUserInfo = userInfo;
	}
	
	/**
	 * 全局退出回调</p>
	 * @param activity 关联的Activity
	 */
	public void onlogout(Activity activity) {}
	
	/**
	 * 获得合法的进程名
	 * @return
	 */
	public String[] getValidProcessNames(){
		return null;
	}
	
	/**
	 * 是否是合法进程
	 * @return
	 */
	public boolean isProcessValid() {
		String process[] = getValidProcessNames();
		if (process == null || process.length == 0) {
			return true;
		}
		String processName = ProcessUtils.getProcessName(this);
		for (int i = 0; i < process.length; i++) {
			if (process != null && process[i].equals(processName)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Object getSystemService(String name) {
		IServiceManager manager = ServiceProvider.getServiceProvider().getServiceManager();
		if(manager != null){
			Object service = manager.getService(name);
			if(service != null)
				return service;
		}
		return super.getSystemService(name);
	}

    /**
     * 网络状态改变
     */
    public void onNetworkChange(){
        LogUtil.v("BaseApp", "onNetworkChange");
    }

    //监听器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
                onNetworkChange();
            }
        }
    };
}
