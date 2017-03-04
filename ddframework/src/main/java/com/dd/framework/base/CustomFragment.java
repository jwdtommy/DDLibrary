package com.dd.framework.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dd.framework.services.network.ApiException;
import com.dd.framework.services.network.CustomSubscriberListener;
import com.dd.framework.utils.UIUtils;
import com.dd.framework.widgets.BottomView;
import com.dd.framework.widgets.EmptyView;
import com.dd.framework.widgets.LoadingView;
import com.dd.framework.widgets.RootView;
import com.dd.framework.widgets.TopView;
import com.dd.framework.widgets.UIViewFactory;
/**
 * Created by J.Tommy on 17/2/14.
 */
public abstract class CustomFragment<UIHelper, ApiHelper> extends BaseFragment implements CustomSubscriberListener<BaseResult> {
	private RootView mRootView;
	private TopView mTopView;
	private BottomView mBottomView;
	private View mCenterView;
	private EmptyView mEmptyView;
	private LoadingView mLoadingView;
	private UIHelper mUIHelper;
	private ApiHelper mApiHelper;

	public RootView getRootView() {
		return mRootView;
	}

	public TopView getTopView() {
		return mTopView;
	}

	public BottomView getBottomView() {
		return mBottomView;
	}

	public View getCenterView() {
		return mCenterView;
	}

	@Override
	public final View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = new RootView(getContext());
		/*
		top
		 */
		mTopView = UIViewFactory.getInstance().buildTopView(this);
		if (mTopView != null) {
			UIUtils.setViewId(mTopView);
			RelativeLayout.LayoutParams paramsTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			mRootView.addView(mTopView, paramsTop);
		}
		/*
		bottom
         */
		mBottomView = UIViewFactory.getInstance().buildBottomView(this);
		if (mBottomView != null) {
			UIUtils.setViewId(mBottomView);
			RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			paramsBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mRootView.addView(mBottomView, paramsBottom);
		}
				/*
		emptyView
		 */
		mEmptyView = UIViewFactory.getInstance().buildEmptyView(this);
		mEmptyView.setVisibility(View.GONE);
		UIUtils.setViewId(mEmptyView);
		RelativeLayout.LayoutParams paramsEmpty = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		if (mTopView != null) {
			paramsEmpty.addRule(RelativeLayout.BELOW, mTopView.getId());
		}
		if (mBottomView != null) {
			paramsEmpty.addRule(RelativeLayout.ABOVE, mBottomView.getId());
		}
		mRootView.addView(mEmptyView, paramsEmpty);
		/*
		center
		 */
		mCenterView = onCreateCenterViewImpl(savedInstanceState);
		UIUtils.setViewId(mCenterView);
		RelativeLayout.LayoutParams paramsCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		if (mTopView != null) {
			paramsCenter.addRule(RelativeLayout.BELOW, mTopView.getId());
		}
		if (mBottomView != null) {
			paramsCenter.addRule(RelativeLayout.ABOVE, mBottomView.getId());
		}
		mRootView.addView(mCenterView, paramsCenter);


		/*
		loadingView
		 */
		mLoadingView = new LoadingView(getActivity());
		mLoadingView.setVisibility(View.GONE);
		UIUtils.setViewId(mLoadingView);
		RelativeLayout.LayoutParams paramsLoading = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		if (mTopView != null) {
			paramsEmpty.addRule(RelativeLayout.BELOW, mTopView.getId());
		}
		if (mBottomView != null) {
			paramsEmpty.addRule(RelativeLayout.ABOVE, mBottomView.getId());
		}
		mRootView.addView(mLoadingView, paramsLoading);
		return mRootView;
	}

	public abstract View onCreateCenterViewImpl(@Nullable Bundle savedInstanceState);

	public void loadData(int action, boolean isFirst) {
		onProcess(action);
	}

	private final void onProcess(int action) {
		onProcessImpl(action);
	}

	public void onProcessImpl(int action) {

	}

	@Override
	public void onLoading() {
		mLoadingView.setVisibility(View.VISIBLE);
	}

	@Override
	public final void onNext(BaseResult baseResult) {
		mEmptyView.setVisibility(View.GONE);
	}

	@Override
	public final void onCompleted() {
		mLoadingView.setVisibility(View.GONE);
	}

	@Override
	public void onError(Throwable e) {
		if(e instanceof ApiException){
			Toast.makeText(getActivity(),((ApiException)e).getApiExceptionMessage(),2000).show();
		}
		e.printStackTrace();
		mEmptyView.setVisibility(View.VISIBLE);
	}
}
