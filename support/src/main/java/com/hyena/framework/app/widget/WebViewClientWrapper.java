/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebViewClient包装类
 * @author yangzc
 *
 */
@SuppressLint("NewApi")
public class WebViewClientWrapper extends WebViewClient {

	protected WebViewClient mWrapper;
	
	public WebViewClientWrapper(){
		super();
	}

	public WebViewClientWrapper(WebViewClient wrapper) {
		setWebViewClient(wrapper);
	}
	
	public void setWebViewClient(WebViewClient wrapper){
		this.mWrapper = wrapper;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (mWrapper != null) {
			return mWrapper.shouldOverrideUrlLoading(view, url);
		}
		return super.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		if (mWrapper != null) {
			mWrapper.onPageStarted(view, url, favicon);
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		if (mWrapper != null) {
			mWrapper.onPageFinished(view, url);
		}
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);
		if (mWrapper != null) {
			mWrapper.onLoadResource(view, url);
		}
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		if (mWrapper != null) {
			return mWrapper.shouldInterceptRequest(view, url);
		}
		return super.shouldInterceptRequest(view, url);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTooManyRedirects(WebView view, Message cancelMsg,
			Message continueMsg) {
		super.onTooManyRedirects(view, cancelMsg, continueMsg);
		if (mWrapper != null) {
			mWrapper.onTooManyRedirects(view, cancelMsg, continueMsg);
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		if (mWrapper != null) {
			mWrapper.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		super.onFormResubmission(view, dontResend, resend);
		if (mWrapper != null) {
			mWrapper.onFormResubmission(view, dontResend, resend);
		}
	}

	@Override
	public void doUpdateVisitedHistory(WebView view, String url,
			boolean isReload) {
		super.doUpdateVisitedHistory(view, url, isReload);
		if (mWrapper != null) {
			mWrapper.doUpdateVisitedHistory(view, url, isReload);
		}
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		super.onReceivedSslError(view, handler, error);
		if (mWrapper != null) {
			mWrapper.onReceivedSslError(view, handler, error);
		}
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		super.onReceivedHttpAuthRequest(view, handler, host, realm);
		if (mWrapper != null) {
			mWrapper.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		if (mWrapper != null) {
			return mWrapper.shouldOverrideKeyEvent(view, event);
		}
		return super.shouldOverrideKeyEvent(view, event);
	}

	@Override
	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		super.onUnhandledKeyEvent(view, event);
		if (mWrapper != null) {
			mWrapper.onUnhandledKeyEvent(view, event);
		}
	}

	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		super.onScaleChanged(view, oldScale, newScale);
		if (mWrapper != null) {
			mWrapper.onScaleChanged(view, oldScale, newScale);
		}
	}

	@Override
	public void onReceivedLoginRequest(WebView view, String realm,
			String account, String args) {
		super.onReceivedLoginRequest(view, realm, account, args);
		if (mWrapper != null) {
			mWrapper.onReceivedLoginRequest(view, realm, account, args);
		}
	}
	
	
}
