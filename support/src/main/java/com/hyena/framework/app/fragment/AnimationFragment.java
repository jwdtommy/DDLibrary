/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

/**
 * 动画场景基类
 * @author yangzc
 * @param <T>
 */
public class AnimationFragment<T extends BaseUIFragmentHelper> extends
		BaseUIFragment<T> {

	/*
	 * 执行流程如下：
	 * onViewCreated -> getInAnimator -> refresh启动 -> loadDatas
	 * stopRefresh -> getOutAnimator -> finish
	 */
	
	private static final int MSG_UPDATE = 1;
	private Handler mIOHandler = null;
	
	/**
	 * 入场动画
	 * @return
	 */
	public Animator getInAnimator(){
		return null;
	}
	
	/**
	 * 出场动画
	 * @return
	 */
	public Animator getOutAnimator(){
		return null;
	}
	
	/**
	 * 加载数据
	 */
	public void loadDatas(){}
	
	/**
	 * UI刷新
	 */
	public void onUpdate(){}
	
	/**
	 * 开始刷新
	 */
	private void startUpdate(){
		if (mIOHandler != null) {
			mIOHandler.obtainMessage(MSG_UPDATE).sendToTarget();
		}
	}
	
	/**
	 * 停止刷新
	 */
	private void stopUpdate(){
		if (mIOHandler != null) {
			mIOHandler.removeMessages(MSG_UPDATE);
		}
	}
	
	/**
	 * 收到循环消息
	 * @param msg
	 */
	private void handleMessageImpl(Message msg){
		int what = msg.what;
		switch (what) {
		case MSG_UPDATE:
		{
			//刷新
			onUpdate();
			//发送下一个通知
			if (mIOHandler != null) {
				Message next = mIOHandler.obtainMessage(MSG_UPDATE);
				mIOHandler.sendMessageDelayed(next, 50);
			}
			break;
		}
		default:
			break;
		}
	}
	
	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
	}
	
	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
	}
	
	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return super.onCreateViewImpl(savedInstanceState);
	}
	
	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		HandlerThread thread = new HandlerThread(getClass().getSimpleName());
		thread.start();
		mIOHandler = new Handler(thread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				handleMessageImpl(msg);
			}
		};
		
		Animator inAnimator = getInAnimator();
		if (inAnimator != null) {
			inAnimator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {}
				
				@Override
				public void onAnimationRepeat(Animator animation) {}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					//开始执行刷新
					startUpdate();
					//开始加载数据
					loadDatas();
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {}
			});
			inAnimator.start();
		} else {
			//开始执行刷新
			startUpdate();
		}
	}
	
	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
		stopUpdate();
	}
	
	@Override
	public void onResumeImpl() {
		super.onResumeImpl();
	}
	
	@Override
	public void onPauseImpl() {
		super.onPauseImpl();
	}
	
	@Override
	public void finish() {
		Animator outAnimator = getOutAnimator();
		if (outAnimator != null) {
			outAnimator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {}
				@Override
				public void onAnimationRepeat(Animator animation) {}
				@Override
				public void onAnimationEnd(Animator animation) {
					AnimationFragment.this.onPanelClosed(null);
				}
				@Override
				public void onAnimationCancel(Animator animation) {}
			});
			outAnimator.start();
		} else {
			super.finish();
		}
	}
}
