/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network.listener;

/**
 * 可关闭Http监听器
 * @author yangzc
 */
public class CancelableListener extends DataHttpListener {

	private boolean mIsCancel = false;
	public void cancel(){
		mIsCancel = true;
	}
	@Override
	public boolean onStart(long startPos, long contentLength) {
		if(mIsCancel)
			return false;
		
		return super.onStart(startPos, contentLength);
	}
	@Override
	public boolean onAdvance(byte[] buffer, int offset, int len) {
		if(mIsCancel)
			return false;
		
		return super.onAdvance(buffer, offset, len);
	}
	@Override
	public boolean onCompleted() {
		if(mIsCancel)
			return false;
		
		return super.onCompleted();
	}
	@Override
	public void onError(int statusCode) {
		super.onError(statusCode);
	}
	@Override
	public boolean onReady(String url) {
		if(mIsCancel)
			return false;
		
		return super.onReady(url);
	}
	@Override
	public boolean onRelease() {
		if(mIsCancel)
			return false;
		
		return super.onRelease();
	}
	
	
}
