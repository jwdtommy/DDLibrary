/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network;

import com.hyena.framework.bean.KeyValuePair;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Http请求执行器
 * @author yangzc
 */
public interface HttpExecutor {
	
	/**
	 * Http请求参数
	 */
	public static class HttpRequestParams {
		//开始位置
		public long mStartPos = 0;
		//超时时间
		public int mTimeout = 0;
		//buffer长度
		public int mBufferSize = 1024;
		//请求头
		public HashMap<String, String> mHeader;
		//请求参数
		public ArrayList<KeyValuePair> mParams;
		//Post请求时的上传的字节数组
		public HashMap<String, ByteFile> mByteFileMap;
		//Post请求时直接上传的流
		public OutputStreamHandler mOsHandler;
		//判断是否走代理
		public boolean isProxy;
	}
	
	/**
	 * Post提交表单数据
	 */
	public static class ByteFile {
		public String mFileName;
		public String mMimeType = "application/octet-stream";
		public byte[] mBytes;
		
		public ByteFile(String fileName, String mimeType, byte[] bytes){
			this.mFileName = fileName;
			this.mMimeType = mimeType;
			this.mBytes = bytes;
		}
	}

	/**
	 * Post上传文件
	 */
	public static interface OutputStreamHandler {
		public void writeTo(OutputStream os) throws IOException;
		public long getLength();
	}

	/**
	 * 代理地址
	 */
	public static class ProxyHost {
		public String mHost;
		public int mPort;

		public ProxyHost() {}

		public ProxyHost(String host, int port) {
			this.mHost = host;
			this.mPort = port;
		}
	}

	/**
	 * 发起Get请求
	 * @param url 请求的URL
	 * @param params 请求参数
	 * @param httpListener 监听器
	 * @return
	 */
	public HttpResult doGet(String url, HttpRequestParams params, HttpListener httpListener);
	
	/**
	 * 发起Post请求
	 * @param url 请求的URL
	 * @param params 请求参数
	 * @param httpListener 监听器
	 * @return
	 */
	public HttpResult doPost(String url, HttpRequestParams params, HttpListener httpListener);
	
}
