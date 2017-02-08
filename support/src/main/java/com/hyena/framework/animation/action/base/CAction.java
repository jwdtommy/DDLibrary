package com.hyena.framework.animation.action.base;

import com.hyena.framework.animation.sprite.CActionNode;
import com.hyena.framework.clientlog.LogUtil;

/**
 * 动作基类
 * @author yangzc
 */
public class CAction {
	
	private static final String LOG_TAG = CAction.class.getSimpleName();
	//精灵
	protected CActionNode actionNode;
	protected volatile boolean mIsStarted = false;
	
    public static CAction action() {
        return new CAction();
    }

    protected CAction() {
    	LogUtil.v(LOG_TAG, "init");
    }

    /**
     * 开始播放动画
     */
    public void start(CActionNode actionNode) {
    	LogUtil.v(LOG_TAG, "start");
    	mIsStarted = true;
    	this.actionNode = actionNode;
    }

    /**
     * 停止播放动画
     */
    public void stop() {
    	mIsStarted = false;
    	LogUtil.v(LOG_TAG, "stop");
    }

    /**
     * 是否完成
     * @return
     */
    public boolean isDone() {
        return !mIsStarted;
    }

    /**
     * 更新动作
     * @param dt 更新间隔
     */
    public void update(float dt) {
//    	LogUtil.v(LOG_TAG, "update");
    }
    
    public void reset(){
    	
    }
}
