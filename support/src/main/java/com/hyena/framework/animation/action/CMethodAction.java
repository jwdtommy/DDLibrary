package com.hyena.framework.animation.action;

import com.hyena.framework.animation.action.base.CIntervalAction;

/**
 * 方法动作
 * 用于顺序动画的状态回调
 * @author yangzc
 *
 */
public class CMethodAction extends CIntervalAction {

	private volatile boolean mIsDone = false;
	
	private CMethodCallBack mMethodCallBack;
	
	protected CMethodAction(CMethodCallBack callBack) {
		super(0);
		this.mMethodCallBack = callBack;
		mIsDone = false;
	}
	
	public static CMethodAction create(CMethodCallBack callBack){
		return new CMethodAction(callBack);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if(!mIsStarted)
			return;
		
		try {
			if(mMethodCallBack != null){
				mMethodCallBack.onCallBack();
			}
		} catch (Exception e) {
		}
		
		mIsDone = true;
	}
	
	@Override
	public boolean isDone() {
		return mIsDone;
	}
	
	@Override
	public synchronized void reset() {
		super.reset();
		mIsDone = false;
	}
	
	@Override
	public void stop() {
		super.stop();
		mIsDone = true;
	}
	
	public static interface CMethodCallBack {
		public void onCallBack();
	}
}
