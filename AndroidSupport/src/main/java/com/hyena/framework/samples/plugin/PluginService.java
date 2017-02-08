package com.hyena.framework.samples.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hyena.framework.clientlog.LogUtil;

/**
 * Created by yangzc on 16/6/30.
 */
public class PluginService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.v("yangzc", "PluginService--onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
