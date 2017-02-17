package com.dd.news.services;

import com.dd.framework.base.BaseApp;
import com.dd.framework.services.ServiceManager;
import com.dd.news.services.net.APIServiceImpl;
/**
 * Created by J.Tommy on 17/2/13.
 */
public class AppServiceManager extends ServiceManager {
	@Override
	public void init(BaseApp baseApp) {
		super.init(baseApp);
		registerAppServices();
	}
	public void registerAppServices() {
		registerService(APIServiceImpl.class);//网络服务
	}
}
