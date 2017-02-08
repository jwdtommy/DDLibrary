/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.hyena.framework.app.adapter.SingleTypeAdapter;

/**
 * 通用TabBar </p>
 * 功能有限，使用时需要注意相关逻辑 </p>
 * 初始化Adapter -> setAdapter 
 * 数据改变 notifyDataSetChange
 * 
 * @author yangzc
 *
 * @param <T>
 */
public class CommonTabBar<T> extends LinearLayout {

	private SingleTypeAdapter<T> mAdapter;

	private View mSelectView;
	
	public CommonTabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
	}

	public CommonTabBar(Context context) {
		super(context);
		setOrientation(LinearLayout.HORIZONTAL);
	}

	/**
	 * 设置列表适配器
	 * @param adapter
	 */
	public void setAdapter(SingleTypeAdapter<T> adapter) {
		this.mAdapter = adapter;
		notifyDataSetChange();
	}

	/**
	 * 通知数据变化
	 */
	public void notifyDataSetChange() {
		if (mAdapter == null) {
			return;
		}
		if (getChildCount() != mAdapter.getCount()) {
			removeAllViews();
			for (int i = 0; i < mAdapter.getCount(); i++) {
				final int postion = i;
				View view = mAdapter.getView(i, null, this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				params.weight = 1;
				addView(view, params);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						T t = mAdapter.getItem(postion);
						if (mClickListener != null) {
							mClickListener.onTabBarClick(t);
						}
						mSelectView = v;
						refreshStatus();
					}
				});
			}
		} else {
			for (int i = 0; i < mAdapter.getCount(); i++) {
				mAdapter.getView(i, getChildAt(i), this);
			}
		}
	}
	
	/**
	 * 刷新状态
	 */
	private void refreshStatus(){
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child == mSelectView) {
				child.setSelected(true);
			}else {
				child.setSelected(false);
			}
		}
	}
	
	private OnTabBarClickListener<T> mClickListener;
	public void setOnTabBarClickListener(OnTabBarClickListener<T> listener){
		this.mClickListener = listener;
	}
	
	public static interface OnTabBarClickListener<T> {
		public void onTabBarClick(T item);
	}
}
