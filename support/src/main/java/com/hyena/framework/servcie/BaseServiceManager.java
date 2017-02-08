/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.servcie;

import java.util.HashMap;

import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.PlayerBusServiceImpl;
import com.hyena.framework.servcie.bus.BusService;
import com.hyena.framework.servcie.bus.BusServiceImpl;
import com.hyena.framework.utils.BaseApp;

/**
 * 服务基类
 * @author yangzc on 15/8/24.
 */
public class BaseServiceManager implements IServiceManager {

    //动态服务列表
    private HashMap<String, Object> mServices = new HashMap<String, Object>();

    public BaseServiceManager() {
		super();
    }

    /**
     * 初始化所有的框架服务
     */
    public void initFrameServices(){
        //服务消息总线服务
        BusService busService = new BusServiceImpl(BaseApp.getAppContext());
        registService(BusService.BUS_SERVICE_NAME, busService);

        //播放控制总线服务
        PlayerBusService playerBusService = new PlayerBusServiceImpl(BaseApp.getAppContext());
        registService(PlayerBusService.BUS_SERVICE_NAME, playerBusService);
        //注册总线消息
        busService.addBusServiceAction(playerBusService);
    }

    @Override
    public Object getService(String name) {
        return mServices.get(name);
    }

    @Override
    public void releaseAll() {
        //释放网络服务
    }

    @Override
    public void registService(String name, Object service) {
        if(mServices.containsKey(name))
            return;
        mServices.put(name, service);
    }

    @Override
    public void unRegistService(String name) {
        mServices.remove(name);
    }

}
