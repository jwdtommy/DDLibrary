package com.dd.framework.base;

import android.app.Application;

import com.dd.framework.image.FrescoImageLoader;
import com.dd.framework.services.IService;
import com.dd.framework.services.ServiceManager;
import com.dd.framework.utils.UIUtils;

/**
 * Created by J.Tommy on 16/11/8.
 */
public abstract class BaseApp extends Application {
	private static BaseApp app;
	private ServiceManager mServiceManager;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		//todo if(processs==mainProcess)再执行init;
		init();
	}

	private void init() {
		FrescoImageLoader.initalize(this, this.getCacheDir());
		UIUtils.init(this);
		// todo other framework things
		initApp();
		mServiceManager = configServerManager();
		mServiceManager.init(this);
	}

	public ServiceManager getServiceManager() {
		if (mServiceManager == null) {
			throw new RuntimeException("please config ServiceManager in you app!");
		}
		return mServiceManager;
	}

	public abstract ServiceManager configServerManager();


	public abstract void initApp();

	public static BaseApp getInstance() {
		return app;
	}

	public <S extends IService> S getService(Class<S> serviceClass) {
		if (mServiceManager == null) {
			throw new RuntimeException("please config ServiceManager in you app!");
		}
		return mServiceManager.getService(serviceClass);
	}
}
