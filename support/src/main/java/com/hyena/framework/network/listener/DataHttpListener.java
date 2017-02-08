/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network.listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;

import com.hyena.framework.network.HttpListener;

/**
 * 保存网络数据为本地字节数组
 * @author yangzc
 */
public class DataHttpListener implements HttpListener {

	private ByteArrayOutputStream mByteArrayOutputStream;
	private byte mByteDatas[];
	
	public byte[] getData(){
		return mByteDatas;
	}
	
	@Override
	public boolean onStart(long startPos, long contentLength) {
		mByteArrayOutputStream = new ByteArrayOutputStream();
		return true;
	}

	@Override
	public boolean onAdvance(byte[] buffer, int offset, int len) {
		if(mByteArrayOutputStream != null)
			mByteArrayOutputStream.write(buffer, offset, len);
		return true;
	}

	@Override
	public boolean onCompleted() {
		if(mByteArrayOutputStream != null)
			mByteDatas = mByteArrayOutputStream.toByteArray();
		return true;
	}

	@Override
	public void onError(int statusCode) {
		if(mByteArrayOutputStream != null){
			try {
				mByteArrayOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mByteArrayOutputStream = null;
		}
	}

	@Override
	public boolean onReady(String url) {
		return true;
	}

	@Override
	public boolean onResponse(InputStream is, OutputStream os
			, int statusCode, String mimeType, String encoding, long contentLength
			, boolean repeatable, boolean isTrunk) {
		return false;
	}

	@Override
	public boolean onRelease() {
		return true;
	}

//	public String getGzipString()
//			throws IllegalStateException, IOException {
//		if (mByteDatas == null) {
//			return null;
//		}
//		GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(mByteDatas));
//		StringWriter writer = new StringWriter();
//
//		char[] buffer = new char[1024];
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, "UTF-8"));
//			int n;
//			while ((n = reader.read(buffer)) != -1) {
//				writer.write(buffer, 0, n);
//			}
//		} finally {
//			is.close();
//		}
//		return writer.toString();
//	}

}
