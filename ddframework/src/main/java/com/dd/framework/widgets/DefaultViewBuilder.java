package com.dd.framework.widgets;

import com.dd.framework.base.CustomFragment;

/**
 * Created by J.Tommy on 17/2/14.
 */

public class DefaultViewBuilder implements ViewBuilder{
	@Override
	public TopView buildTopView(CustomFragment fragment) {
		return new CommonTopView(fragment.getActivity());
	}

	@Override
	public EmptyView buildEmptyView(CustomFragment fragment) {
		return new EmptyView(fragment.getActivity());
	}

	@Override
	public LoadingView buildLoadingView(CustomFragment fragment) {
		return new LoadingView(fragment.getActivity());
	}

	@Override
	public BottomView buildBottomView(CustomFragment fragment) {
		return new BottomView(fragment.getActivity());
	}
}
