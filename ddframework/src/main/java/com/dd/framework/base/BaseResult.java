package com.dd.framework.base;

/**
 * Created by J.Tommy on 17/2/16.
 */

public abstract class BaseResult<T> {
	public final static int OK = 0;//数据返回正常
	public final static int NO_DATA = -1;//无数据
	public final static int ERROR_NETWORK_UNAVAILABLE = -2;//网络不可用
	public final static int ERROR_INVALID_PARAMETER = -3;//参数有误
	public final static int ERROR_AUTHORIZATION_FAIL = -4;//授权问题
	public final static int ERROR_NETWORK_TIMEOUT = -5;//网络超时

	public  int error_code;
	public String error_message;
	public T data;

    public BaseResult(){
	}
	/*
		将自身业务的errorcode转化为框架中对应的错误代码
	 */
	public abstract void transfrom();


}
