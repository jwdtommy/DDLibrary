package com.dd.framework.widgets;

import com.dd.framework.base.CustomFragment;

/**
 * Created by J.Tommy on 17/2/14.
 */

public interface ViewBuilder {
	TopView buildTopView(CustomFragment fragment);
	EmptyView buildEmptyView(CustomFragment fragment);
	LoadingView buildLoadingView(CustomFragment fragment);
	BottomView buildBottomView(CustomFragment fragment);
}
