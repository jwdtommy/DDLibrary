/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network;

import org.apache.http.protocol.HTTP;

import com.hyena.framework.network.listener.DataHttpListener;

/**
 * Http返回结果
 * @author yangzc
 */
public class HttpResult {

	//请求的URL
	public String mUrl;
	//服务IP
	public String mIp;
	//状态码
	public int mStatusCode = -1;
	//开始位置
	public long mStartPos = -1;
	//文件长度
	public long mContentLength;
	//错误原因
	public int mErrorCode = HttpError.SUCCESS;
	//是否是Gzip
	public boolean mIsGzip = false;
	//是否是trunked数据
	public boolean mIsTrunked = false;
	//是否下载成功
	public boolean isSuccess(){
		return mErrorCode == HttpError.SUCCESS;
	}
	
	public long mApTs = 0;//地址解析时间
	public long mReqTs = 0;//响应时间
	public long mReadTs = 0;//读取时间
	
	
	//是否是因为Cancel而导致的失败
	public boolean isCanceled(){
		if(mErrorCode == HttpError.ERROR_CANCEL_BEGIN
				|| mErrorCode == HttpError.ERROR_CANCEL_ADVANCE
				|| mErrorCode == HttpError.ERROR_CANCEL_READY)
			return true;
		return false;
	}

	//获取Cancel原因
	public String getCancelReason(){
		switch(mErrorCode){
		case HttpError.ERROR_CANCEL_BEGIN:{
			return "开始下载时";
		}
		case HttpError.ERROR_CANCEL_ADVANCE:{
			return "正在读取时";
		}
		case HttpError.ERROR_CANCEL_READY:{
			return "准备开始时";
		}
		}
		return "未知";
	}
	
	//Http请求监听器
	public HttpListener mHttpListener;
	
	//获取Http返回的数据
	public String getResult(){
		if(mHttpListener != null && mHttpListener instanceof DataHttpListener){
			DataHttpListener dataHttpListener = (DataHttpListener) mHttpListener;
			
			if(!isSuccess() || dataHttpListener.getData() == null)
				return null;
			
			try {
				return new String(dataHttpListener.getData(), HTTP.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
