package com.dd.fakefans.base;

import android.app.Application;

import com.dd.fakefans.fresco.FrescoImageLoader;
import com.dd.fakefans.utils.UIUtils;

/**
 * Created by adong on 16/8/21.
 */
public class App extends Application {
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        FrescoImageLoader.initalize(this,this.getCacheDir());
        UIUtils.init(this);
    }
    public static App getInstance()
    {
        return  instance;
    }
}
