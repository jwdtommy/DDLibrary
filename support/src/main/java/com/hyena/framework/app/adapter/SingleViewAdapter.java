package com.hyena.framework.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 但类型View通用列表适配器
 * @author yangzc
 *
 * @param <T>
 */
public abstract class SingleViewAdapter <T> extends BaseAdapter {

	protected int mType;
	protected T mItem;
	protected Context mContext;
	protected BaseAdapter mParentAdapter;
	
	public SingleViewAdapter(Context context, BaseAdapter parent, int type, T item){
		this.mContext = context;
		this.mParentAdapter = parent;
		this.mType = type;
		this.mItem = item;
	}
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public T getItem(int position) {
		return mItem;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return mType;
	}
	
	public BaseAdapter getParentAdapter(){
		return mParentAdapter;
	}
	
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent) ;
	

}
