package com.dd.fakefans.base;

import android.app.Application;
import com.dd.fakefans.fresco.FrescoImageLoader;
import com.dd.fakefans.utils.UIUtils;

/**
 * Created by J.Tommy on 16/11/8.
 */
public abstract class BaseApp extends Application {
	private static Application app;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		//todo if(processs==mainProcess)再执行init;
		init();
	}

	public void init() {
		FrescoImageLoader.initalize(this, this.getCacheDir());
		UIUtils.init(this);
	}

	public static Application getInstance() {
		return app;
	}
}
