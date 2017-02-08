package com.hyena.framework.samples;

import android.os.Environment;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.clientlog.Logger;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.BaseApp;

/**
 * Created by yangzc on 16/4/19.
 */
public class App extends BaseApp {

    @Override
    public void initApp() {
        super.initApp();
        LogUtil.setDebug(true);
        LogUtil.setLevel(Logger.DO_NOT_WRITE_LOG);
        FrameworkConfig.getConfig().setAppRootDir(Environment.getExternalStorageDirectory());
        ServiceProvider.getServiceProvider().registServiceManager(new BoxServiceManager());
    }
}
