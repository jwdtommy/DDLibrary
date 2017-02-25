package com.dd.news;

import com.dd.framework.base.BaseApp;
import com.dd.framework.base.CustomFragment;
import com.dd.framework.services.ServiceManager;
import com.dd.framework.widgets.BottomView;
import com.dd.framework.widgets.CommonTopView;
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
	}
	@Override
	public ServiceManager configServerManager() {
		return new AppServiceManager();
	}
}
