/**
 * Copyright (C) 2014 The BaiduMusicFramework Project
 */
package com.hyena.framework.animation;

import android.content.Context;
import android.graphics.Rect;
import android.view.View.OnTouchListener;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年9月26日 下午4:47:21
 * 
 */
public interface RenderView {

	public void setDirector(Director director);

	/**
	 * 开始刷新
	 */
	void startRefresh();
	
	/**
	 * 停止刷新
	 */
	void stopRefresh();

	void forceRefresh();
	
	/**
	 * 获得View上下文
	 * @return
	 */
	Context getContext();
	
	/**
	 *  渲染器是否显示
	 * @return
	 */
	boolean isShown();
	
	/**
	 * 设置触摸监听事件
	 * @param listener
	 */
	void setOnTouchListener(OnTouchListener listener);
	
	/**
	 * 设置窗口大小改变事件
	 * @param listener
	 */
	void setSizeChangeListener(SizeChangeListener listener);
	
	public static interface SizeChangeListener {
		public void onSizeChange(Rect rect);
	}
}
