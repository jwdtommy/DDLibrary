/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.debug.observer;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * List点击状态实例
 * @author yangzc
 *
 */
public abstract class BaseOnItemClickListener implements OnItemClickListener {

	private String mListName;
	public BaseOnItemClickListener(String listName) {
		this.mListName = listName;
	}
	
	public String getListName(){
		return mListName;
	}
	
	@Override
	public final void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		onItemClickImpl(parent, view, position, id);
	}

	public abstract void onItemClickImpl(AdapterView<?> parent, View view, int position,
			long id);
}
