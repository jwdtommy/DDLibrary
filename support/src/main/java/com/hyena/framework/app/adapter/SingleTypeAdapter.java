/**
 * Copyright (C) 2014 The KnowboxTeacher Project
 */
package com.hyena.framework.app.adapter;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * 单类型Adapter
 * @author yangzc
 *
 * @param <T>
 */
public abstract class SingleTypeAdapter<T> extends BaseAdapter {

	private List<T> mItems;
	protected Context mContext;
	
	public SingleTypeAdapter(Context context) {
		super();
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		if(mItems == null)
			return 0;
		return mItems.size();
	}

	@Override
	public T getItem(int position) {
		if(mItems == null)
			return null;
		if(position < mItems.size())
			return mItems.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItems(List<T> items){
		this.mItems = items;
		notifyDataSetChanged();
	}
	
	public void addItems(List<T> items) {
		if(mItems != null) {
			this.mItems.addAll(items);
			notifyDataSetChanged();
		}
	}
	
	public List<T> getItems(){
		return mItems;
	}
	
	public void removeItem(T t){
		if(mItems.contains(t)) {
			mItems.remove(t);
			notifyDataSetChanged();
		}
	}
}
