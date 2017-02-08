package com.hyena.framework.animation.sprite;

import java.util.ArrayList;
import java.util.List;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.action.base.CAction;

/**
 * 可进行动作的节点
 * @author yangzc
 *
 */
public abstract class CActionNode extends CNode {

	public abstract void rotate(float degrees);
	
	public abstract void setSkew(float skewX, float skewY);
	
	public abstract void setAnchor(float x, float y);
	
	public abstract void setScale(float sx, float sy);
	
	public abstract void setAlpha(int alpha);
	

	//动作列表
	private List<CAction> mActions;

	public CActionNode(Director director) {
		super(director);
	}

	/**
	 * 执行动作
	 * @param action
	 */
	public synchronized void runAction(CAction action){
		if(action == null)
			return;
		if(mActions == null)
			mActions = new ArrayList<CAction>();
		
		mActions.add(action);
		action.start(this);
	}
	
	@Override
	public synchronized void update(float dt) {
		super.update(dt);
		
		if(mActions != null){
			List<CAction> rmAction = new ArrayList<CAction>();
			for(int i =0; i< mActions.size(); i++){
				CAction action = mActions.get(i);
				if(action == null)
					continue;
				action.update(dt);
				if(action.isDone())
					rmAction.add(action);
			}
			mActions.removeAll(rmAction);
		}
	}
	
}
