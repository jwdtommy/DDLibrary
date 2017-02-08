/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Observable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

/**
 * 网页列表
 * @author yangzc
 */
public class HybirdWebListView extends HybirdWebView {

	private WebViewListAdapter<?> mAdapter;
	private SparseArray<String> mRowKeys = new SparseArray<String>(2000);
	
	public HybirdWebListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HybirdWebListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HybirdWebListView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 初始化webView
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void initView() {
        setWebViewClient(new WebViewClient());
        setWebChromeClient(new WebChromeClient());
		getSettings().setJavaScriptEnabled(true);
		getSettings().setUseWideViewPort(true);
		getSettings().setAppCacheEnabled(false);
		getSettings().setAllowFileAccess(false);
	}

	/**
	 * 设置列表适配器
	 * @param adapter
	 */
	public void setAdapter(WebViewListAdapter<?> adapter) {
		if (mAdapter != null) {
			// 解注册数据观察器
			mAdapter.unregisterDataSetObserver(dataSetObserver);
		}
		this.mAdapter = adapter;
		// 注册数据观察器
		this.mAdapter.registerDataSetObserver(dataSetObserver);
	}

	 //数据监听器
	private WebDataSetObserver dataSetObserver = new WebDataSetObserver() {

		@Override
		public void onSetItems() {
			mRowKeys.clear();
			if (mAdapter != null) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < mAdapter.getCount(); i++) {
					String rowHtml = mAdapter.getHtml(i);
					if (TextUtils.isEmpty(rowHtml)) {
						continue;
					}
					mRowKeys.setValueAt(i, mAdapter.getItemId(i));
					buffer.append(rowHtml);
				}
				//提交所有的条目
				replaceAllRow(buffer.toString());
			}
		}
		
		@Override
		public void onAddItems(int startIndex) {
			if (mAdapter != null) {
				StringBuffer buffer = new StringBuffer();
				for (int i = startIndex; i < mAdapter.getCount(); i++) {
					String rowHtml = mAdapter.getHtml(i);
					if (TextUtils.isEmpty(rowHtml)) {
						continue;
					}
					mRowKeys.setValueAt(i, mAdapter.getItemId(i));
					buffer.append(rowHtml);
				}
				//扩充所有的条目
				appendRows(buffer.toString());
			}
		}
		
		@Override
		public void onRemoveItem(int index) {
			onSetItems();
		}

		@Override
		public void onDataSetChange() {
			if (mAdapter != null) {
				for (int i = 0; i < mAdapter.getCount(); i++) {
					String itemId = mAdapter.getItemId(i);
					if (TextUtils.isEmpty(itemId)) {
						continue;
					}
					String cacheKey = mRowKeys.valueAt(i);
					if (itemId.equals(cacheKey)) {
						continue;
					}else {
						mRowKeys.setValueAt(i, itemId);
						replaceOrAddRow(i, mAdapter.getHtml(i));
					}
				}
				//check size
				runJs("checkRows", mAdapter.getCount() + "");
			}
		}
	};

	/**
	 * 替换或者添加行
	 * @param position
	 * @param html
	 */
	private void replaceOrAddRow(int position, String html) {
		try {
			String encode = URLEncoder.encode(html.replace(" ","n1b1sp"), HTTP.UTF_8);
			runJs("replaceOrAddRow", position + "", encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 替换所有行
	 * @param html
	 */
	private void replaceAllRow(final String html) {
        try {
            String encode = URLEncoder.encode(html.replace(" ","n1b1sp"), HTTP.UTF_8);
            runJs("replaceAllRow", encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * 添加数据
	 * @param html
	 */
	private void appendRows(String html){
		try {
            String encode = URLEncoder.encode(html.replace(" ","n1b1sp"), HTTP.UTF_8);
            runJs("appendRows", encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	}

	/**
	 * 执行js
	 * @param method
	 * @param params
	 */
	@SuppressLint("NewApi")
	public void runJs(String method, String... params) {
		
		final StringBuffer jsBuffer = new StringBuffer();
		jsBuffer.append("javascript:");
		jsBuffer.append(method);
		jsBuffer.append("(");
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				if (i == 0) {
					jsBuffer.append("'" + params[i] + "'");
				} else {
					jsBuffer.append(",'" + params[i] + "'");
				}
			}
		}
		jsBuffer.append(")");
		
		if(android.os.Build.VERSION.SDK_INT >= 19) {
			evaluateJavascript(jsBuffer.toString(), new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
				}
			});
		} else {
			loadUrl(jsBuffer.toString());
		}
	}

	/**
	 * WebViewListAdapter
	 * 
	 * @param <T>
	 */
	public static abstract class WebViewListAdapter<T> {

		private final WebDataSetObservable mDataSetObservable = new WebDataSetObservable();
		
		private List<T> mItems;
		protected Context mContext;

		public WebViewListAdapter(Context context) {
			super();
			this.mContext = context;
		}

		public int getCount() {
			if (mItems == null)
				return 0;
			return mItems.size();
		}

		public T getItem(int position) {
			if (mItems == null)
				return null;
			if (position < mItems.size())
				return mItems.get(position);
			return null;
		}

		/**
		 * 设置数据
		 * @param items
		 */
		public void setItems(List<T> items) {
			this.mItems = items;
			if (mDataSetObservable != null)
				mDataSetObservable.notifySetItems();
		}

		/**
		 * 增加数据
		 * @param items
		 */
		public void addItems(List<T> items) {
			if (mItems != null) {
				int startIndex = getCount();
				this.mItems.addAll(items);
				
				if (mDataSetObservable != null)
					mDataSetObservable.notifyAddItems(startIndex);
			}
		}

		public List<T> getItems() {
			return mItems;
		}

		public void removeItem(T t) {
			if (mItems.contains(t)) {
				int index = mItems.indexOf(t);
				mItems.remove(t);
				
				if (mDataSetObservable != null)
					mDataSetObservable.notifyRemoveItem(index);
			}
		}
		
		public void removeItems(List<T> items){
			if(items != null && !items.isEmpty()) {
				mItems.removeAll(items);
				
				if (mDataSetObservable != null)
					mDataSetObservable.notifySetItems();
			}
		}
		
		public void removeAllItems() {
			if(mItems != null) {
				mItems.clear();
				if (mDataSetObservable != null)
					mDataSetObservable.notifySetItems();
			}
		}

		/**
		 * 获得模板内容
		 * @param position 位置
		 * @return 模板内容
		 */
		public abstract String getHtml(int position);
		
		/**
		 * 获得某一项ID
		 * @param position
		 * @return
		 */
		public abstract String getItemId(int position);

		/**
		 * 通知数据改变
		 */
		public void notifyDataSetChange() {
			if (mDataSetObservable != null)
				mDataSetObservable.notifyDataSetChange();
		}

		public void registerDataSetObserver(WebDataSetObserver observer) {
			mDataSetObservable.registerObserver(observer);
		}

		public void unregisterDataSetObserver(WebDataSetObserver observer) {
			mDataSetObservable.unregisterObserver(observer);
		}
	}
	
	public static class WebDataSetObservable extends Observable<WebDataSetObserver> {
		
		/**
	     * 设置数据
	     */
	    public void notifySetItems(){
	    	synchronized(mObservers) {
	            for (int i = mObservers.size() - 1; i >= 0; i--) {
	                mObservers.get(i).onSetItems();
	            }
	        }
	    }
		
		/**
		 * 添加数据
		 * @param startIndex
		 */
	    public void notifyAddItems(int startIndex){
	    	synchronized(mObservers) {
	            for (int i = mObservers.size() - 1; i >= 0; i--) {
	                mObservers.get(i).onAddItems(startIndex);
	            }
	        }
	    }
	    
	    /**
		 * 删除数据
		 * @param startIndex
		 */
	    public void notifyRemoveItem(int index){
	    	synchronized(mObservers) {
	            for (int i = mObservers.size() - 1; i >= 0; i--) {
	                mObservers.get(i).onRemoveItem(index);
	            }
	        }
	    }
	    
	    /**
	     * 数据集发生变化
	     */
	    public void notifyDataSetChange() {
	        synchronized(mObservers) {
	            for (int i = mObservers.size() - 1; i >= 0; i--) {
	                mObservers.get(i).onDataSetChange();
	            }
	        }
	    }
	}

	public static abstract class WebDataSetObserver {
		
		/**
		 * 数据集发生改变
		 */
		public abstract void onDataSetChange();

		/**
		 * 添加Items
		 * @param startIndex 原数据的结尾位置
		 */
		public abstract void onAddItems(int startIndex);
		

		/**
		 * 删除Item
		 * @param index 删除数据索引
		 */
		public abstract void onRemoveItem(int index);
		
		/**
		 * 初始化位置
		 */
		public abstract void onSetItems();
	}
}
