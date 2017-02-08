package com.hyena.framework.datacache;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.datacache.cache.CacheEntry;
import com.hyena.framework.datacache.cache.CacheExpiredException;
import com.hyena.framework.datacache.cache.CacheUncachedException;
import com.hyena.framework.datacache.cache.DataCache;
import com.hyena.framework.network.HttpExecutor;
import com.hyena.framework.network.HttpExecutor.OutputStreamHandler;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.network.listener.DataHttpListener;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.HttpHelper;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据获取器
 * 
 * @param <T>
 */
public class DataAcquirer<T extends BaseObject> {
	/** 缓存默认最长时间2小时 */
	private static final long VALID_TIME = 2 * 60 * 60 * 1000;
	private static final String TAG = "DataAcquirer";
	
	/**
	 * 获取数据<br/>
	 * 优先从缓存中取数据；<br/>
	 * 当缓存中无相应数据或者数据已过期时，从网络获取数据并缓存；
	 * 
	 * @param url 网络请求url
	 * @param t 数据对象
	 * @return 填充数据后的数据对象
	 */
	public T acquire(String url, T t) {
		return acquire(url, t, VALID_TIME);
	}

	/**
	 * 获取数据<br/>
	 * 优先从缓存中取数据；<br/>
	 * 当缓存中无相应数据或者数据已过期时，从网络获取数据并缓存；
	 * 
	 * @param url 网络请求url
	 * @param t 数据对象
	 * @param cacheValidTime 缓存时间(以毫秒计，小于等于0时不缓存)
	 * @return 填充数据后的数据对象
	 */
	@SuppressWarnings("unchecked")
	public T acquire(String url,T t, long cacheValidTime) {
		if (t == null || TextUtils.isEmpty(url))
			return t;
		url = addMD5KeyUrl(url);
		String cacheKey = createCacheKey(url);
		if (cacheValidTime <= 0
				&& NetworkProvider.getNetworkProvider().getNetworkSensor()
						.isNetworkAvailable()) {
			t = getImpl(url, t);
			//非网络问题
			if (t.getStatusCode() == HttpStatus.SC_OK) {
				//TODO:如果服务器返回状态码正常，默认为数据没有问题
				DataCache.getInstance(BaseApp.getAppContext()).put(cacheKey, t, VALID_TIME);
				return t;
			}
		}

		CacheEntry entry = new CacheEntry(t);
		try {
			t = (T) DataCache.getInstance(BaseApp.getAppContext()).get(cacheKey, entry);
			LogUtil.d(TAG, "load from cache key : " + url + ">>" + cacheKey);
		} catch (CacheExpiredException e) {
			LogUtil.d(TAG, "CacheExpiredException : " + url);
			t = (T) entry.getObject();
			t = getImpl(url, t);
			DataCache.getInstance(BaseApp.getAppContext()).put(cacheKey, t, cacheValidTime);
			if (t != null)
				t.setErrorCode(BaseObject.OK);
		} catch (CacheUncachedException e) {
			LogUtil.d(TAG, "CacheUncachedException : " + url);
			t = getImpl(url, t);
			DataCache.getInstance(BaseApp.getAppContext()).put(cacheKey, t, cacheValidTime);
		}
		return t;
	}

	public T get(String url, final T t){
		if (t == null || TextUtils.isEmpty(url))
			return t;
		url = addMD5KeyUrl(url);
		return getImpl(url, t);
	}

	/**
	 * 获取缓存数据
	 * @param url
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T acquireCache(String url, T t) {
		if (t == null || TextUtils.isEmpty(url))
			return t;
		url = addMD5KeyUrl(url);
		String cacheKey = createCacheKey(url);
		CacheEntry entry = new CacheEntry(t);
		try {
			t = (T) DataCache.getInstance(BaseApp.getAppContext()).get(cacheKey, entry);
			LogUtil.d(TAG, "load from cache key : " + url + ">>" + cacheKey);
		} catch (CacheExpiredException e) {
			LogUtil.d(TAG, "CacheExpiredException : " + url);
			t = (T) entry.getObject();
			if (t != null)
				t.setErrorCode(BaseObject.OK);
		} catch (CacheUncachedException e) {
		}
		return t;

	}

	/**
	 * 获取数据
	 * @param url 数据请求URL
	 * @param t 解析到的数据对象
	 * @return 获取数据并解析后的数据对象
	 */
	private T getImpl(String url, T t) {
		if (t == null)
			return t;
		if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
			t.setErrorCode(BaseObject.ERROR_NETWORK_UNAVAILABLE);
			return t;
		}
		t.resetState();
		LogUtil.d(TAG, "load from net key : " + url);
		try {
			HttpProvider httpProvider = new HttpProvider();
			DataHttpListener httpListener = new DataHttpListener();
			HttpResult result = httpProvider.doGet(url, HttpHelper.TIMEOUT, -1, httpListener, new KeyValuePair("Accept-Encoding", "gzip"));
			read(result, httpListener, t);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 发起post请求
	 * 
	 * @param url
	 * @param byteFileMap
	 * @param params
	 * @param t
	 * @return
	 */
	public T post(String url, HashMap<String, HttpExecutor.ByteFile> byteFileMap,
				  final ArrayList<KeyValuePair> params, final T t) {
		if (t == null)
			return t;
		if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
			t.setErrorCode(BaseObject.ERROR_NETWORK_UNAVAILABLE);
			return t;
		}
		t.resetState();
		try {
			HttpProvider httpProvider = new HttpProvider();
			DataHttpListener httpListener = new DataHttpListener();
			HttpResult result = httpProvider.doPost(addMd5Url(url, params), params, byteFileMap, httpListener, new KeyValuePair("Accept-Encoding", "gzip"));
			read(result, httpListener, t);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 发送数据
	 * @param url 数据请求URL
	 * @param params POST参数
	 * @param t 解析到的数据对象
	 * @return 获取数据并解析后的数据对象
	 */
	public T post(final String url,
			final ArrayList<KeyValuePair> params, final T t) {
		return post(url, null, params, t);
	}

	/**
	 * 发送数据
	 * @param url
	 * @param osHandler
	 * @param t
	 * @return
	 */
	public T post(String url, OutputStreamHandler osHandler, final T t) {
		if (t == null)
			return t;
		if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
			t.setErrorCode(BaseObject.ERROR_NETWORK_UNAVAILABLE);
			return t;
		}
		t.resetState();
		try {
			HttpProvider httpProvider = new HttpProvider();
			DataHttpListener httpListener = new DataHttpListener();
			HttpResult result = httpProvider.doPost(addMD5KeyUrl(url), osHandler,
					httpListener, new KeyValuePair("Accept-Encoding",
							"gzip"));
			read(result, httpListener, t);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 没有data 直接提交json
	 * post上传接口
	 * @param url
	 * @param jsonBody 必须为json
	 * @param t
	 * @return
	 */
	public T post(String url, final String jsonBody,
				  final T t) {
		if (TextUtils.isEmpty(jsonBody))
			return t;
		
		url = addMD5KeyUrl(url);

		if (t == null)
			return t;
		if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
			t.setErrorCode(BaseObject.ERROR_NETWORK_UNAVAILABLE);
			return t;
		}
		final String data = addPostKeyUrl(jsonBody);
		LogUtil.d("DefaultHttpExecutor", data);
		t.resetState();
		try {
			HttpProvider httpProvider = new HttpProvider();
			DataHttpListener httpListener = new DataHttpListener();
			HttpResult result = httpProvider.doPost(url,
					new OutputStreamHandler() {
						@Override
						public void writeTo(OutputStream os) throws IOException {
							os.write(data.getBytes());
						}

						@Override
						public long getLength() {
							return data.getBytes().length;
						}
					}, httpListener, new KeyValuePair("Accept-Encoding",
							"gzip"), new KeyValuePair("Content-Type",
							"application/json"));
			read(result, httpListener, t);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return t;

	}

	/**
	 * 读取数据并解析
	 * 
	 * @param result
	 * @param t
	 * @throws IOException
	 */
	private void read(HttpResult result, DataHttpListener listener, T t)
			throws IOException {
		if (result == null)
			return;
		t.setStatusCode(result.mStatusCode);
		if (result.isSuccess() && listener.getData() != null) {
			String content = result.getResult();
			if (t != null)
				t.parse(content, false);
		} else {
			switch (result.mStatusCode) {
			case 400:
				if (t != null)
					t.setErrorCode(BaseObject.ERROR_INVALID_PARAMETER);
				break;
			case 401:
				if (t != null)
					t.setErrorCode(BaseObject.ERROR_AUTHORIZATION_FAIL);
				break;
			case 408:
				if (t != null)
					t.setErrorCode(BaseObject.ERROR_NETWORK_TIMEOUT);
				break;
			default:
				if (t != null)
					t.setErrorCode(BaseObject.ERROR_HTTP_REQUEST_ERROR);
			}
		}
	}

	/**
	 * 根据URL及参数构造网络请求缓存key
	 * @param url URL
	 * @return 缓存key
	 */
	private String createCacheKey(String url) {
		return MD5Util.encode(url);
	}

	@SuppressLint("DefaultLocale")
	private static String addMD5KeyUrl(String url) {
		if (url.indexOf("?") == -1) {
			return url;
		}
		URL uri = null;
		try {
			uri = new URL(url);
		} catch (MalformedURLException e) {
			return url;
		}
		String query = uri.getQuery();
		try {
			if (FrameworkConfig.getConfig().getAppType() == FrameworkConfig.APP_SUSUAN) {
				query = URLDecoder.decode(query, HTTP.UTF_8);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String getUrl = url + "&kbparam=" + MD5Util.encode(query
				+ FrameworkConfig.getConfig().getGetEncodeKey()).toUpperCase();
		return getUrl;
	}

	@SuppressLint("DefaultLocale")
	private String addMd5Url(String url, ArrayList<KeyValuePair> params){
		if(params != null){
			String appendParams = "";
			for (int i = 0; i < params.size(); i++) {
				String name = params.get(i).getKey();
				if("data".equals(name)){
					appendParams = MD5Util.encode("data=" + params.get(i).getValue()
							+ FrameworkConfig.getConfig().getGetEncodeKey()).toUpperCase();
					break;
				}
			}

			if(!TextUtils.isEmpty(appendParams)) {
				if (url.indexOf("?") == -1) {
					url += "?";
				} else {
					url += "&";
				}
				url += "kbparam=" + appendParams;
			}
		}
		return url;
	}

	@SuppressLint("DefaultLocale")
	private static String addPostKeyUrl(String json) {
		try {
			StringBuffer buffer = new StringBuffer(json);
			String kbparam = MD5Util.encode(json + FrameworkConfig.getConfig().getPostEncodeKey()).toUpperCase();
			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append(",\"kbparam\":");
			buffer.append("\"" + kbparam + "\"");
			buffer.append("}");
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
