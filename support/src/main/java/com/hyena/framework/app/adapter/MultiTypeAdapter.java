package com.hyena.framework.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * 多类型列表适配器
 * @author yangzc
 */
public abstract class MultiTypeAdapter extends BaseAdapter {

	//适配器列表
	private List<ListAdapter> mAdapters = new ArrayList<ListAdapter>();
	//上下文
	private Context mContext;
	
	public MultiTypeAdapter(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 获得上下文
	 * @return
	 */
	public Context getContext(){
		return mContext;
	}

	/**
	 * 添加适配器
	 * @param adapter
	 */
	public void addAdapter(final BaseAdapter adapter) {
		mAdapters.add(adapter);
		adapter.registerDataSetObserver(mAdapterObserver);
	}

	/**
	 * 清空数据
	 */
	public void removeAllAdapters() {
		if (mAdapters != null) {
			if (mAdapters != null && !mAdapters.isEmpty()) {
				for (int i = 0; i < mAdapters.size(); i++) {
					mAdapters.get(i).unregisterDataSetObserver(mAdapterObserver);
				}
			}
			mAdapters.clear();
			notifyDataSetChanged();
		}
	}
	
	/**
	 * 设置适配器
	 * @param adapters
	 */
	public void setAdapters(List<ListAdapter> adapters){
		this.mAdapters = adapters;
		if (mAdapters != null && !mAdapters.isEmpty()) {
			for (int i = 0; i < mAdapters.size(); i++) {
				mAdapters.get(i).unregisterDataSetObserver(mAdapterObserver);
				mAdapters.get(i).registerDataSetObserver(mAdapterObserver);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mAdapters == null)
			return 0;
		
		int cnt = 0;
		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			cnt += adapter.getCount();
		}
		return cnt;
	}

	@Override
	public Object getItem(int position) {
		if (mAdapters == null) {
			return null;
		}
		int p = position;
		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			int cnt = adapter.getCount();
			if (p < cnt) {
				return adapter.getItem(p);
			}
			p -= cnt;
		}
		return null;

	}

	@Override
	public long getItemId(int position) {
		if (mAdapters == null)
			return 0;
		int p = position;

		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			int cnt = adapter.getCount();
			if (p < cnt) {
				return adapter.getItemId(p);
			}
			p -= cnt;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		if (mAdapters == null || mAdapters.size() == 0) {
			return 1;
		}
		int cnt = 0;

		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			int typecnt = Math.max(adapter.getViewTypeCount(), 1);
			cnt += typecnt;
		}
		return cnt;
	}

	@Override
	public int getItemViewType(int position) {
		if (mAdapters == null) {
			return super.getItemViewType(position);
		}
		int p = position;
		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			int cnt = adapter.getCount();
			if (p < cnt) {
				return adapter.getItemViewType(p);
			}
			p -= cnt;
		}
		return super.getItemViewType(position);
	}

	@Override
	public boolean isEnabled(int position) {
		if (mAdapters == null) {
			return super.isEnabled(position);
		}
		int p = position;

		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			int cnt = adapter.getCount();
			if (p < cnt) {
				return adapter.isEnabled(p);
			}
			p -= cnt;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mAdapters == null) {
			return null;
		}
		int p = position;

		for (int i = 0; i < mAdapters.size(); i++) {
			ListAdapter adapter = mAdapters.get(i);
			int cnt = adapter.getCount();
			if (p < cnt) {
				return adapter.getView(p, convertView, parent);
			}
			p -= cnt;
		}
		return null;
	}

	//数据集观察者
	private DataSetObserver mAdapterObserver = new DataSetObserver(){
		@Override
		public void onChanged() {
			MultiTypeAdapter.this.notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			MultiTypeAdapter.this.notifyDataSetInvalidated();
		}
	};
}
