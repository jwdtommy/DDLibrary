/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 获取更多列表
 * @author yangzc
 */
public class LoadMoreListView extends ListView implements OnScrollListener {

	private boolean mLastItemVisible;
	private OnScrollListener mOnScrollListener;
	private OnLastItemVisibleListener mOnLastItemVisibleListener;

//	private View mFootView;
//	private View mLoadingView;
//	private ImageView mIvProgress;
//	private Animation mAnimation;

	private ListLoadingMoreFooter mFootView;

	public LoadMoreListView(Context context) {
		super(context);
		super.setOnScrollListener(this);
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnScrollListener(this);
	}

	public void initFooter(ListLoadingMoreFooter footer){
//		mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_loading);
//		mAnimation.setInterpolator(new LinearInterpolator());
//
//		mFootView = View.inflate(getContext(), R.layout.xlistview_footer, null);
//		mLoadingView = mFootView.findViewById(R.id.xlistview_footer_loading);
//		mIvProgress = (ImageView) mFootView.findViewById(R.id.progressbarFootId);
//		mIvProgress.startAnimation(mAnimation);
		mFootView = footer;
		addFooterView(footer);
		setLoadingFootVisible(false);
	}

	@Override
	public final void onScroll(final AbsListView view,
							   final int firstVisibleItem, final int visibleItemCount,
							   final int totalItemCount) {
		if (!mEnableLoadMore)
			return;

		if (null != mOnLastItemVisibleListener) {
			mLastItemVisible = (totalItemCount > 0)
					&& (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
		}
		if (null != mOnScrollListener) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}

	public void setLoadStatus(boolean isLoading){
		setLoadingFootVisible(isLoading);
	}

	public void setLoadingFootVisible(boolean isVisible){
		if (getAdapter() == null || !mEnableLoadMore)
			return;

		mFootView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		if(isVisible) {
			mFootView.setPadding(0, 0, 0, 0);
			setSelection(getAdapter().getCount() - 1);
		} else {
			mFootView.setPadding(0, -mFootView.getHeight(), 0, 0);
		}
	}

	@Override
	public void onScrollStateChanged(final AbsListView view,
									 final int state) {
		if (!mEnableLoadMore)
			return;

		if (state == OnScrollListener.SCROLL_STATE_IDLE
				&& null != mOnLastItemVisibleListener && mLastItemVisible) {
			mOnLastItemVisibleListener.onLastItemVisible();
		}

		if (null != mOnScrollListener) {
			mOnScrollListener.onScrollStateChanged(view, state);
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	public final void setOnLastItemVisibleListener(
			OnLastItemVisibleListener listener) {
		mOnLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public interface OnLastItemVisibleListener {
		void onLastItemVisible();
	}

	private boolean mEnableLoadMore = true;

	public void setEnableLoadMore(boolean enableLoadMore){
		this.mEnableLoadMore = enableLoadMore;
	}
}
