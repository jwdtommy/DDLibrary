package com.dd.framework.widgets;

import com.dd.framework.base.CustomFragment;

public class UIViewFactory {
	private static UIViewFactory mUIViewFactory;
	private ViewBuilder mViewBuilder = new DefaultViewBuilder();

	private UIViewFactory() {

	}

	public static UIViewFactory getInstance() {
		if (mUIViewFactory == null) {
			mUIViewFactory = new UIViewFactory();
		}
		return mUIViewFactory;
	}

	public void registerViewBuilder(ViewBuilder viewBuilder) {
		this.mViewBuilder = viewBuilder;
	}

	public TopView buildTopView(CustomFragment fragment) {
		if (mViewBuilder == null) {
			throw new RuntimeException("please register ViewBuilder");
		}
		return mViewBuilder.buildTopView(fragment);
	}

	public EmptyView buildEmptyView(CustomFragment fragment) {
		if (mViewBuilder == null) {
			throw new RuntimeException("please register ViewBuilder");
		}
		return mViewBuilder.buildEmptyView(fragment);
	}

	public LoadingView buildLoadingView(CustomFragment fragment) {
		if (mViewBuilder == null) {
			throw new RuntimeException("please register ViewBuilder");
		}
		return mViewBuilder.buildLoadingView(fragment);
	}

	public BottomView buildBottomView(CustomFragment fragment) {
		if (mViewBuilder == null) {
			throw new RuntimeException("please register ViewBuilder");
		}
		return mViewBuilder.buildBottomView(fragment);
	}
}
