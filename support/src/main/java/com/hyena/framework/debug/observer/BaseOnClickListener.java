/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.debug.observer;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * 点击状态实例
 * @author yangzc
 *
 */
public abstract class BaseOnClickListener implements OnClickListener {

	private String mItemName;
	
	public BaseOnClickListener(String name){
		this.mItemName = name;
	}

	public String getItemName(){
		return mItemName;
	}

	@Override
	public final void onClick(View v) {
		onClickImpl(v);
	}
	
	/**
	 * View点击实例
	 * @param v
	 */
	public abstract void onClickImpl(View v);
}
