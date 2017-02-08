package com.hyena.framework.network.executor;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 不验证证书有效性
 * 
 * @author wangzengyang 2012-11-2
 * 
 */
public class EasyX509TrustManager implements X509TrustManager {

	/**
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],
	 *      String authType)
	 */
	public void checkClientTrusted(X509Certificate[] certificates,
			String authType) throws CertificateException {
	}

	/**
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],
	 *      String authType)
	 */
	public void checkServerTrusted(X509Certificate[] certificates,
			String authType) throws CertificateException {
	}

	/**
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}