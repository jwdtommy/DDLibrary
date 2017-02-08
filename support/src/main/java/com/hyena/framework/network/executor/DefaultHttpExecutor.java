/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network.executor;

import android.text.TextUtils;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.debug.DebugUtils;
import com.hyena.framework.network.HttpError;
import com.hyena.framework.network.HttpExecutor;
import com.hyena.framework.network.HttpListener;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.network.NetworkSensor;
import com.hyena.framework.network.utils.EntityUtil;
import com.hyena.framework.network.utils.HttpUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * 默认HTTP请求具体实现类
 * 使用HttpClient实现Http请求
 * @author yangzc
 *
 */
public class DefaultHttpExecutor implements HttpExecutor {

	private static final String TAG = "DefaultHttpExecutor";

	private static final int DEFAULT_TIMEOUT = 30;

	@Override
	public HttpResult doGet(String url, HttpRequestParams params,
			HttpListener httpListener) {
		return execute(true, url, params, httpListener);
	}

	@Override
	public HttpResult doPost(String url, HttpRequestParams params,
			HttpListener httpListener) {
		return execute(false, url, params, httpListener);
	}

	private HttpResult execute(boolean isGet, String url, final HttpRequestParams params,
			HttpListener listener) {

		debug("method: " + (isGet ? "Get" : "Post") + ", execute :" + url);
		int timeout = DEFAULT_TIMEOUT;
		//如果外部没有设置超时时间，则使用默认30s超时
		if(params != null && params.mTimeout > 0)
			timeout = params.mTimeout;

		HttpResult result = new HttpResult();
		result.mHttpListener = listener;

		//检查网络
		NetworkSensor networkSensor = NetworkProvider.getNetworkProvider().getNetworkSensor();
		if(networkSensor == null || !networkSensor.isNetworkAvailable()){
			result.mErrorCode = HttpError.ERROR_NO_AVAILABLE_NETWORK;
			if(listener != null)
				try {
					listener.onError(HttpError.ERROR_NO_AVAILABLE_NETWORK);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			return result;
		}

		if(TextUtils.isEmpty(url)){
			if(listener != null)
				try {
					listener.onError(HttpError.ERROR_URL_EMPTY);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			result.mErrorCode = HttpError.ERROR_URL_EMPTY;
			return result;
		}

		InputStream is = null;
		HttpUriRequest request = null;
		HttpClient client = null;
		try {
			if(listener != null && !listener.onReady(url)){
				result.mErrorCode = HttpError.ERROR_CANCEL_READY;
				return result;
			}
			//重新构筑URL
			String new_url = networkSensor.rebuildUrl(url);
			if(!TextUtils.isEmpty(new_url)){
				url = new_url;
			}

//			String ip = "";
//			URL urlobj = new URL(url);
//			String host = urlobj.getHost();
			long start = System.currentTimeMillis();
//			boolean isWifiNetwork = false;
//			if(isWifiNetwork){
//				ip = Inet4Address.getByName(host).getHostAddress();
//				result.mApTs = System.currentTimeMillis() - start;//地址解析时间
//				url = url.replace(host, ip);
//				debug("host " + host + ", ip :" + ip);
//				debug("url: " + url);
//			}
			result.mUrl = url;//同步请求的URL
//			result.mIp = ip;//请求IP地址

			if(isGet){//Get请求
				//重新定义请求字符串
				if(params != null && params.mParams != null)
					url = HttpUtils.encodeUrl(url, params.mParams);
				request = new HttpGet(url);
			} else {
				request = new HttpPost(url);
				HttpEntity entity = null;
				if(params != null && params.mOsHandler != null){
					entity = new CustomHttpEntity(params.mOsHandler);
				} else {
					entity = new MultipartEntity();
					//如果提交元素包含字节数组，则使用混排方式上传
					if(params != null && params.mByteFileMap != null) {
						if(params != null && params.mParams != null){
							for (KeyValuePair param : params.mParams) {
								String paramName = param.getKey();
								String paramValue = param.getValue();
								((MultipartEntity)entity).addPart(paramName,
										new StringBody(paramValue, Charset.forName(HTTP.UTF_8)));
							}
						}

						Iterator<String> iterator = params.mByteFileMap.keySet().iterator();
						while(iterator.hasNext()){
							String key = iterator.next();
							ByteFile file = params.mByteFileMap.get(key);
							if(TextUtils.isEmpty(file.mMimeType) || TextUtils.isEmpty(file.mFileName)){
								continue;
							}else{
								((MultipartEntity)entity).addPart(key, new ByteArrayBody(file.mBytes, file.mMimeType,
									file.mFileName));
							}
						}
					} else {
						//简单提交Post请求
						if(params != null && params.mParams != null){
							List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
							for (KeyValuePair param : params.mParams) {
								String paramName = param.getKey();
								String paramValue = param.getValue();
								values.add(new BasicNameValuePair(paramName, paramValue));
							}
							entity = new UrlEncodedFormEntity(values, HTTP.UTF_8);
						}
					}
				}

				((HttpPost)request).setEntity(entity);
			}

			long startPos = 0;
			if(params != null && params.mStartPos > 0){
				startPos = params.mStartPos;
				request.addHeader("RANGE", "bytes=" + startPos + "-");
				result.mStartPos = startPos;
			}

			//添加默认头
			List<KeyValuePair> commonHeaders = networkSensor.getCommonHeaders(url, params.isProxy);
			if(commonHeaders != null){
				for(KeyValuePair pair: commonHeaders){
					request.addHeader(pair.getKey(), pair.getValue());
				}
			}
			//添加自定义头
			if(params != null && params.mHeader != null){
				Iterator<String> iterator = params.mHeader.keySet().iterator();
				while(iterator.hasNext()){
					String name = iterator.next();
					String value = params.mHeader.get(name);
					request.addHeader(name, value);
				}
			}
//			request.addHeader("Accept-Encoding", "gzip");
//			if(isWifiNetwork)
//				request.addHeader("Host", host);
			client = HttpUtils.createHttpClient(timeout);

			//添加默认代理
			ProxyHost proxy = networkSensor.getProxyHost(url, params.isProxy);
			if(proxy != null){//添加自定义代理
				HttpHost httpHost = new HttpHost(proxy.mHost, proxy.mPort);
				client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
			}

			start = System.currentTimeMillis();
			final HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			is = httpEntity.getContent();
			if (httpEntity != null) {
				if (listener != null && listener.onResponse(is,
						null,
						statusCode,
						httpEntity.getContentType() == null ? "" : httpEntity.getContentType().getValue(),
						httpEntity.getContentEncoding() == null ? "" : httpEntity.getContentEncoding().getValue(),
						httpEntity.getContentLength(),
						httpEntity.isRepeatable(),
						httpEntity.isChunked())) {
					result.mErrorCode = HttpError.ERROR_CANCEL_RESPONSE;
					return result;
				}
			}

			result.mReqTs = System.currentTimeMillis() - start;//请求响应时间
			result.mStatusCode = statusCode;//同步状态码

			debug("statusCode :" + statusCode);
			if(statusCode == HttpStatus.SC_OK
					|| statusCode == HttpStatus.SC_PARTIAL_CONTENT){

				if(LogUtil.isDebug()){
					Header headers[] = response.getAllHeaders();
					if(headers != null && headers.length > 0){
						for(int i=0; i< headers.length; i++){
							debug("header name: " + headers[i].getName() + ", value: " + headers[i].getValue());
						}
					}
				}

				long contentLength = response.getEntity().getContentLength();
				result.mContentLength = contentLength;//同步网络数据长度

				Header encoder = response.getLastHeader("Transfer-Encoding");
				if(encoder != null){
					if("chunked".equalsIgnoreCase(encoder.getValue())){
						result.mIsTrunked = true;
					}
				}

				boolean isGzip = EntityUtil.isGZIPed(response.getEntity());
				result.mIsGzip = isGzip;
				debug("contentLength : " + contentLength + ", trunked : " + result.mIsTrunked + ", gzip: " + result.mIsGzip);

				/* 通知开始下载 */
				if(listener != null){
					if(!listener.onStart(startPos, contentLength)){
						//开始时Cancel掉Http请求
						result.mErrorCode = HttpError.ERROR_CANCEL_BEGIN;
						return result;
					}
				}

				if(isGzip){
					is = new GZIPInputStream(response.getEntity().getContent());
				}

				int len = -1;
				//控制buffer长度
				int bufferSize = 1024 * 10;
				if(params != null && params.mBufferSize > 0){
					bufferSize = params.mBufferSize;
				}
				byte buf[] = new byte[bufferSize];
				start = System.currentTimeMillis();
				while((len = is.read(buf, 0, bufferSize)) > 0){
					if(listener != null){
						if(!listener.onAdvance(buf, 0, len)){
							//正在下载过程中Cancel掉Http请求
							result.mErrorCode = HttpError.ERROR_CANCEL_ADVANCE;
							break;
						}
					}
					if(networkSensor != null){
						networkSensor.updateFlowRate(len);
					}
				}
				result.mReadTs = System.currentTimeMillis() - start;//读取时间
				if(listener != null && result.isSuccess()){
					listener.onCompleted();
				}
			}else{
				result.mErrorCode = HttpError.ERROR_STATUS_CODE;
				if(listener != null)
					listener.onError(statusCode);
			}
		} catch (ClientProtocolException e) {
			if(listener != null)
				try {
					listener.onError(HttpError.ERROR_UNKNOWN);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			result.mErrorCode = HttpError.ERROR_UNKNOWN;
			e.printStackTrace();
		} catch (IOException e) {
			if(listener != null)
				try {
					listener.onError(HttpError.ERROR_UNKNOWN);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			result.mErrorCode = HttpError.ERROR_UNKNOWN;
			e.printStackTrace();
		} catch (Throwable e){
			if(listener != null)
				try {
					listener.onError(HttpError.ERROR_UNKNOWN);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			result.mErrorCode = HttpError.ERROR_UNKNOWN;
			e.printStackTrace();
		} finally {
			//关闭流
			if(is != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//关闭请求
			if(request != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
				try{
					request.abort();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			//关闭连接池中的所有连接,不了解应用中是否存在重用连接的情况
			//现在改用ThreadSafeClientConnManager故不需要关闭连接管理器
			if(client != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
				try {
					client.getConnectionManager().shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//通知结束,开始销毁
			if(listener != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
				try {
					listener.onRelease();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		debug("isCancel : " + result.isCanceled() + ", reason :" + result.getCancelReason());
		return result;
	}


	class CustomHttpEntity extends AbstractHttpEntity {

	    private OutputStreamHandler mStreamHandler;
	    private boolean consumed = false;

	    public CustomHttpEntity(final OutputStreamHandler handler) {
	        super();
	        this.mStreamHandler = handler;
	    }

	    @Override
	    public boolean isRepeatable() {
	        return false;
	    }

	    @Override
	    public long getContentLength() {
	        return mStreamHandler.getLength();
	    }

	    @Override
	    public InputStream getContent() throws IOException {
	    	throw new UnsupportedOperationException("Entity template does not implement getContent()");
	    }

	    @Override
	    public void writeTo(final OutputStream outstream) throws IOException {
	    	if (outstream == null) {
	            throw new IllegalArgumentException("Output stream may not be null");
	        }
	    	mStreamHandler.writeTo(outstream);
	        this.consumed = true;
	    }

	    @Override
	    public boolean isStreaming() {
	        return !this.consumed;
	    }

	    @Override
	    public void consumeContent() throws IOException {
	        this.consumed = true;
	    }
	}

	private void debug(String str){
		if(LogUtil.isDebug()){
			LogUtil.v(TAG, str);
		}
		DebugUtils.debugTxt(str);
	}
}
