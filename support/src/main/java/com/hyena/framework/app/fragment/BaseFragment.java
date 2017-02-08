/**
 * Copyright (C) 2014 The KnowboxTeacher Project
 */
package com.hyena.framework.app.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * 基础Fragment
 * @author yangzc
 */
public class BaseFragment extends SafeFragment  {

	/**
	 * 按键点击
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}

	/**
	 * 处理点击事件
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public final boolean handleKeyDown(int keyCode, KeyEvent event){
		try {
			return onKeyDown(keyCode, event);
		} catch (Throwable e) {
		}
		return true;
	}
	
	@Override
	public void onError(Throwable e) {
		super.onError(e);
//		final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//		if(defaultHandler != null)
//			defaultHandler.uncaughtException(Thread.currentThread(), e);
	}

	/**
	 * 跟踪页面访问路径
	 * 
	 * @param view
	 * @param savedInstanceState
	 */
	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
	}

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
	}

	/**
	 * 统计访问次数
	 * 
	 * @param isVisibleToUser
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	/**
	 * 当前Fragment是否可见
	 * @param visible
	 */
	public void setVisibleToUser(boolean visible) {
		debug("Visible: " + visible + "");
	}

	/**
	 * 窗口大小变化
     */
	public void onWindowVisibleSizeChange(Rect rect) {}

	public void onNewIntent(Intent intent) {}
}
