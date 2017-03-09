package com.dd.framework.services;

import com.dd.framework.base.BaseApp;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Created by J.Tommy on 17/2/10.
 */
public class ServiceManager implements IServiceBox {
	private ConcurrentHashMap mServices = new ConcurrentHashMap<>();
	private BaseApp mBaseApp;
	private static ServiceManager mServiceManager;

	public void init(BaseApp baseApp) {
		this.mBaseApp=baseApp;
		registerFrameworkServices();
	}

	public void registerFrameworkServices() {
		//todo
	}

	@Override
	public <S extends IService> S getService(Class<S> serviceClass) {
			return (S)mServices.get(serviceClass.getName());
	}

	@Override
	public void registerService(Class<? extends IService> serviceClass) {
		try {
			mServices.put(serviceClass.getName(),serviceClass.newInstance());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void unregisterService(Class<? extends IService> serviceClass) {
			mServices.remove(serviceClass.getName());
	}
}
