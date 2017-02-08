/*
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.ViewBuilder;

/**
 * 默认的试图构造器
 * @author yangzc
 *
 */
public class DefaultUIViewBuilder implements ViewBuilder {

	@Override
	public TitleBar buildTitleBar(BaseUIFragment<?> fragment) {
		CommonTitleBar titleBar = new CommonTitleBar(fragment.getActivity());
		titleBar.setBaseUIFragment(fragment);
		return titleBar;
	}

	@Override
	public EmptyView buildEmptyView(BaseUIFragment<?> fragment) {
		CommonEmptyView emptyView = new CommonEmptyView(fragment.getActivity());
		emptyView.setBaseUIFragment(fragment);
		return emptyView;
	}

	@Override
	public LoadingView buildLoadingView(BaseUIFragment<?> fragment) {
		CommonLoadingView loadingView = new CommonLoadingView(fragment.getActivity());
		loadingView.setBaseUIFragment(fragment);
		return loadingView;
	}

}
