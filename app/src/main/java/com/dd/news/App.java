package com.dd.news;

import com.dd.framework.base.BaseApp;
import com.dd.framework.base.CustomFragment;
import com.dd.framework.services.ServiceManager;
import com.dd.framework.widgets.BottomView;
import com.dd.framework.widgets.EmptyView;
import com.dd.framework.widgets.LoadingView;
import com.dd.framework.widgets.TopView;
import com.dd.framework.widgets.UIViewFactory;
import com.dd.framework.widgets.ViewBuilder;
import com.dd.news.services.AppServiceManager;
/**
 * Created by J.Tommy on 17/2/10.
 */

public class App extends BaseApp {
	@Override
	public void initApp() {
		UIViewFactory.getInstance().registerViewBuilder(new ViewBuilder() {
			@Override
			public TopView buildTopView(CustomFragment fragment) {
				return null;
			}

			@Override
			public EmptyView buildEmptyView(CustomFragment fragment) {
				return null;
			}

			@Override
			public LoadingView buildLoadingView(CustomFragment fragment) {
				return null;
			}

			@Override
			public BottomView buildBottomView(CustomFragment fragment) {
				return null;
			}
		});
	}
	@Override
	public ServiceManager configServerManager() {
		return new AppServiceManager();
	}
}
