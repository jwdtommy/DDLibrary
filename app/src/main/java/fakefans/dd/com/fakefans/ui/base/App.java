package fakefans.dd.com.fakefans.ui.base;

import android.app.Application;

import fakefans.dd.com.fakefans.fresco.FrescoImageLoader;

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
    }
    public static App getInstance()
    {
        return  instance;
    }
}
