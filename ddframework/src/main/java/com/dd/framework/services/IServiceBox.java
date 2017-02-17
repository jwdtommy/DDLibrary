package com.dd.framework.services;

/**
 * Created by J.Tommy on 17/2/10.
 */

public interface IServiceBox {
	public <S extends IService> S getService(Class<S> serviceClass);

	public void registerService(Class<? extends IService> serviceClass);

	public void unregisterService(Class<? extends IService> serviceClass);
}
