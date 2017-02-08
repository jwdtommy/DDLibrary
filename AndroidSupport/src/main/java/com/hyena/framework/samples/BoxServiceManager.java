package com.hyena.framework.samples;

import com.hyena.framework.servcie.BaseServiceManager;

/**
 * Created by yangzc on 16/9/1.
 */
public class BoxServiceManager extends BaseServiceManager{

    public BoxServiceManager() {
        //初始化框架服务
        initFrameServices();
        //初始化所有服务
        initServices();
    }

    @Override
    public Object getService(String name) {
        return super.getService(name);
    }

    @Override
    public void releaseAll() {
        super.releaseAll();
    }

    /**
     * 初始化所有服务
     */
    private void initServices(){
    }
}
