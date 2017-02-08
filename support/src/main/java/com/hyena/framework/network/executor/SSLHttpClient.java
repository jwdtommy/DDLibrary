package com.hyena.framework.network.executor;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

/**
 * 不验证HTTPS证书有效性的SSL HTTP请求
 * 
 * @author wangzengyang 2012-11-2
 * 
 */
public class SSLHttpClient extends DefaultHttpClient {

	public SSLHttpClient(HttpParams httpParams) {
		super(httpParams);
	}

	public SSLHttpClient() {
		super();
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		// 443是Https的默认端口，如果网站配置的端口不一样，这里要记着改一下
		registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
//		return new ThreadSafeClientConnManager(getParams(), registry);

	}

//	public static SSLHttpClient getInstance() {
//		SSLHttpClient client = new SSLHttpClient();
//		return client;
//	}
	
//	private static SSLHttpClient mSslHttpClient;
	
	public synchronized static SSLHttpClient getInstance(HttpParams params) {
		SSLHttpClient client = new SSLHttpClient(params);
		return client;
//		if(mSslHttpClient == null){
//			mSslHttpClient = new SSLHttpClient(params);
//		}
//		mSslHttpClient.setParams(params);
//		return mSslHttpClient;
	}
}