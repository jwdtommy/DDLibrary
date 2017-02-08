package com.hyena.framework.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;

import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.FileHttpListener;

/**
 * Http请求帮助类
 */
public class HttpHelper {
	
	public static final int TIMEOUT = 60;
	public static final int TIMEOUT_2 = 30;

	/**
	 * 将URL进行编码，使用的字符编码集在Config.ENCODING处设置
	 * 
	 * @param url
	 *            未UrlEncode的url
	 * @return 通过URLEncode转码后的url
	 */
	public static String encodeURL(String url) {
		try {
			return URLEncoder.encode(url, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * 生成URL参数字符串
	 * 
	 * @param params
	 * @return
	 */
	public static String buildParamsString(HashMap<String, String> params) {
		if (CollectionUtil.isEmpty(params))
			return "";
		StringBuilder sb = new StringBuilder();
		String value = null;
		for (String key : params.keySet()) {
			value = params.get(key);
			if (value == null)
				value = "";
			try {
				value = URLEncoder.encode(value, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			sb.append("&").append(key).append("=").append(value);
		}
		return sb.substring(1);
	}

	/**
	 * 保存文件
	 * @param fileUrl
	 * @param filePath
	 * @param listener
	 * @return
	 */
	public static boolean storeFile(String fileUrl, final String filePath, final ProgressListener listener) {
		HttpProvider provider = new HttpProvider();
		//URL无需做非空判断,网络获取模块内部已经处理
		HttpResult result = provider.doGet(fileUrl, 10, new FileHttpListener(filePath){
			private long mTotalLength= -1;
			private long mDownloadLen = 0;
			@Override
			public boolean onStart(long startPos, long contentLength) {
				mTotalLength = contentLength;
				if(mTotalLength < 0)
					return false;
				if(listener != null){
					listener.onStart(mTotalLength);
				}
				return super.onStart(startPos, contentLength);
			}
			
			@Override
			public boolean onAdvance(byte[] buffer, int offset, int len) {
				if(mTotalLength < 0)
					return false;
				boolean isSuccess = super.onAdvance(buffer, offset, len);
				if(isSuccess){
					mDownloadLen += len;
					if(listener != null){
						listener.onAdvance(mDownloadLen, mTotalLength);
					}
				}
				return true;
			}
		});
		if(result != null && result.isSuccess()){
			if(listener != null){
				listener.onComplete(true);
			}
		}else{
			try {
				new File(filePath).delete();	
			} catch (Exception e) {
			}
			if(listener != null){
				listener.onComplete(false);
			}
		}
		return result.isSuccess();
	}
	
	public static interface ProgressListener {
		public void onStart(long total);
		public void onAdvance(long len, long total);
		public void onComplete(boolean isSuccess);
	}
}
