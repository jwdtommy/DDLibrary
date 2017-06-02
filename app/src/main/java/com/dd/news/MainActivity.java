package com.dd.news;

import android.os.Bundle;

import com.dd.framework.base.CustomFragment;
import com.dd.framework.widgets.BottomView;
import com.dd.framework.widgets.CommonTopView;
import com.dd.framework.widgets.EmptyView;
import com.dd.framework.widgets.LoadingView;
import com.dd.framework.widgets.TopView;
import com.dd.framework.widgets.UIViewFactory;
import com.dd.framework.widgets.ViewBuilder;
import com.dd.news.modules.home.HomeFragment;
import com.dd.framework.base.BaseFragment;
import com.dd.framework.base.NavigateActivity;
import com.dd.news.test.SceneFragment;
import com.dd.news.widgets.TDEmptyView;

public class MainActivity extends NavigateActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UIViewFactory.getInstance().registerViewBuilder(new ViewBuilder() {
			@Override
			public TopView buildTopView(CustomFragment fragment) {
				return new CommonTopView(MainActivity.this);
			}

			@Override
			public EmptyView buildEmptyView(CustomFragment fragment) {
				return new TDEmptyView(MainActivity.this);
			}

			@Override
			public LoadingView buildLoadingView(CustomFragment fragment) {
				return null;
			}

			@Override
			public BottomView buildBottomView(CustomFragment fragment) {
				return new BottomView(MainActivity.this);
			}
		});
		addSubFragment(BaseFragment.newFragment(this, HomeFragment.class, null));
	}
}
