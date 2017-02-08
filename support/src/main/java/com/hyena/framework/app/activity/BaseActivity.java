/**
 * Copyright (C) 2014 The KnowboxTeacher Project
 */
package com.hyena.framework.app.activity;

import com.hyena.framework.app.activity.bean.BasicUserInfo;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.BaseApp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Activity基类
 * @author yangzc
 *
 */
public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		//去除标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//强制设置为竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//窗口自适应键盘
		getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		debug(getClass().getSimpleName() + "|" + "msg:onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		debug(getClass().getSimpleName() + "|" + "msg:onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		debug(getClass().getSimpleName() + "|" + "msg:onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		debug(getClass().getSimpleName() + "|" + "msg:onDestroy");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			super.onSaveInstanceState(outState);
		} catch (Throwable e) {
		}
	}

	@Override
	public Object getSystemService(String name) {
		IServiceManager manager = ServiceProvider.getServiceProvider()
				.getServiceManager();
		if (manager != null) {
			Object service = manager.getService(name);
			if (service != null)
				return service;
		}
		return super.getSystemService(name);
	}

	public void debug(String msg){
        LogUtil.v(((Object)this).getClass().getSimpleName(), msg);
	}
	
	//========================== 其他功能 ===================================
	
	/**
	 * 通知登录成功
	 */
	public void notifyLogin(BasicUserInfo userInfo){
		((BaseApp)BaseApp.getAppContext()).onlogin(this, userInfo);
	}
	
	/**
	 * 通知退出登录
	 */
	public void notifyLogout(){
		((BaseApp)BaseApp.getAppContext()).onlogout(this);
	}
}
