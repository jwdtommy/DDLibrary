/**
 * Copyright (C) 2014 The BaiduMusicFramework Project
 */
package com.hyena.framework.animation.action;

import com.hyena.framework.animation.action.base.CIntervalAction;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年10月8日 下午6:57:33
 * 
 */
public class CDelayAction extends CIntervalAction {

	protected CDelayAction(float d) {
		super(d);
	}

	public static CDelayAction create(float delay){
		return new CDelayAction(delay);
	}
	
	@Override
	public synchronized void update(float dt) {
		super.update(dt);
	}
	
	@Override
	public synchronized boolean isDone() {
		return super.isDone();
	}
}
