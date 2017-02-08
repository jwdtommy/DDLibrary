package com.hyena.framework.app.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * 多类型列表适配器
 * @author yangzc
 */

@Deprecated
public class PiecesAdapter extends BaseAdapter {

	protected LinkedHashMap<Integer, ListAdapter> mPieces = new LinkedHashMap<Integer, ListAdapter>();
	protected AdapterObserver mAdapterObserver = new AdapterObserver();
	
	public PiecesAdapter(){
	}
	
	public void addAdapter(final int type, final ListAdapter adapter){
		mPieces.put(type, adapter);
		
		adapter.registerDataSetObserver(mAdapterObserver);
	}
	
	public void clearPieces(){
		if(mPieces != null){
			mPieces.clear();
			notifyDataSetChanged();
		}
	}
	
	public void addView(final int type, View view){
		List<View> views = new ArrayList<View>();
		views.add(view);
		ViewAdapter adapter = new ViewAdapter(views);
		addAdapter(type, adapter);
	}
	
	public int getPieceType(int position){
		if(mPieces == null)
			return -1;
		int p = position;
		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int cnt = adapter.getCount();
			if(p < cnt){
				return key;
			}
			p -= cnt;
		}
		return -1;
	}
	
	public int getTypeOffset(int type){
		if(mPieces == null)
			return 0;
		int cnt = 0;
		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			if(key == type)
				break;
			cnt += adapter.getCount();
		}
		return cnt;
	}
	
	@Override
	public int getCount() {
		if(mPieces == null)
			return 0;
		int cnt = 0;
		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			cnt += adapter.getCount();
		}
		return cnt;
	}
	
	@Override
	public Object getItem(int position) {
		if(mPieces == null){
			return null;
		}
		int p = position;
		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int cnt = adapter.getCount();
			if(p < cnt){
				return adapter.getItem(p);
			}
			p -= cnt;
		}
		return null;

	}
	
	@Override
	public long getItemId(int position) {
		if(mPieces == null)
			return 0;
		int p = position;

		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int cnt = adapter.getCount();
			if(p < cnt){
				return adapter.getItemId(p);
			}
			p -= cnt;
		}
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		if(mPieces == null || mPieces.size() == 0){
			return 1;
		}
		int cnt = 0;

		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int typecnt = Math.max(adapter.getViewTypeCount(), 1);
			cnt += typecnt;
		}
		return cnt;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(mPieces == null){
			return super.getItemViewType(position);
		}
		int p = position;

		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int cnt = adapter.getCount();
			if(p < cnt){
				return adapter.getItemViewType(p);
			}
			p -= cnt;
		}
		return super.getItemViewType(position);
	}

	@Override
	public boolean isEnabled(int position) {
		if(mPieces == null){
			return super.isEnabled(position);
		}
		int p = position;

		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int cnt = adapter.getCount();
			if(p < cnt){
				return adapter.isEnabled(p);
			}
			p -= cnt;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mPieces == null){
			return null;
		}
		int p = position;
		

		Iterator<Integer> iterator = mPieces.keySet().iterator();
		while(iterator.hasNext()){
			Integer key = iterator.next();
			ListAdapter adapter = mPieces.get(key);
			int cnt = adapter.getCount();
			if(p < cnt){
				return adapter.getView(p, convertView, parent);
			}
			p -= cnt;
		}
		return null;
	}
	
	class ViewAdapter extends BaseAdapter {
		List<View> mViews;
		public ViewAdapter(List<View> views){
			this.mViews = views;
		}
		
		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public Object getItem(int position) {
			return mViews.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getItemViewType(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = mViews.get(position);
			return view;
		}
		
	}

	class AdapterObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			PiecesAdapter.this.notifyDataSetChanged();
		}
		
		@Override
		public void onInvalidated() {
			PiecesAdapter.this.notifyDataSetInvalidated();
		}
	}
}
