/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class WebChromeClientWrapper extends WebChromeClient {

	protected WebChromeClient mWrapper;

	public WebChromeClientWrapper() {
	}

	public WebChromeClientWrapper(WebChromeClient wrapper) {
		setWebChromeClient(wrapper);
	}
	
	public void setWebChromeClient(WebChromeClient wrapper){
		this.mWrapper = wrapper;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		if (mWrapper != null) {
			mWrapper.onProgressChanged(view, newProgress);
		}
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
		if (mWrapper != null) {
			mWrapper.onReceivedTitle(view, title);
		}
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		super.onReceivedIcon(view, icon);
		if (mWrapper != null) {
			mWrapper.onReceivedIcon(view, icon);
		}
	}

	@Override
	public void onReceivedTouchIconUrl(WebView view, String url,
			boolean precomposed) {
		super.onReceivedTouchIconUrl(view, url, precomposed);
		if (mWrapper != null) {
			mWrapper.onReceivedTouchIconUrl(view, url, precomposed);
		}
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		super.onShowCustomView(view, callback);
		if (mWrapper != null) {
			mWrapper.onShowCustomView(view, callback);
		}
	}

	@Override
	public void onShowCustomView(View view, int requestedOrientation,
			CustomViewCallback callback) {
		super.onShowCustomView(view, requestedOrientation, callback);
		if (mWrapper != null) {
			mWrapper.onShowCustomView(view, requestedOrientation, callback);
		}
	}

	@Override
	public void onHideCustomView() {
		super.onHideCustomView();
		if (mWrapper != null) {
			mWrapper.onHideCustomView();
		}
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean isDialog,
			boolean isUserGesture, Message resultMsg) {
		if (mWrapper != null) {
			return mWrapper.onCreateWindow(view, isDialog, isUserGesture,
					resultMsg);
		}
		return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
	}

	@Override
	public void onRequestFocus(WebView view) {
		super.onRequestFocus(view);
		if (mWrapper != null) {
			mWrapper.onRequestFocus(view);
		}
	}

	@Override
	public void onCloseWindow(WebView window) {
		super.onCloseWindow(window);
		if (mWrapper != null) {
			mWrapper.onCloseWindow(window);
		}
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			JsResult result) {
		if (mWrapper != null) {
			return mWrapper.onJsAlert(view, url, message, result);
		}
		return super.onJsAlert(view, url, message, result);
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			JsResult result) {
		if (mWrapper != null) {
			return mWrapper.onJsConfirm(view, url, message, result);
		}
		return super.onJsConfirm(view, url, message, result);
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		if (mWrapper != null) {
			return mWrapper
					.onJsPrompt(view, url, message, defaultValue, result);
		}
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message,
			JsResult result) {
		if (mWrapper != null) {
			return mWrapper.onJsBeforeUnload(view, url, message, result);
		}
		return super.onJsBeforeUnload(view, url, message, result);
	}

	@Override
	public void onGeolocationPermissionsShowPrompt(String origin,
			Callback callback) {
		super.onGeolocationPermissionsShowPrompt(origin, callback);
		if (mWrapper != null) {
			mWrapper.onGeolocationPermissionsShowPrompt(origin, callback);
		}
	}

	@Override
	public void onGeolocationPermissionsHidePrompt() {
		super.onGeolocationPermissionsHidePrompt();
		if (mWrapper != null) {
			mWrapper.onGeolocationPermissionsHidePrompt();
		}
	}

	@Override
	public boolean onJsTimeout() {
		if (mWrapper != null) {
			return mWrapper.onJsTimeout();
		}
		return super.onJsTimeout();
	}

	@Override
	public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		super.onConsoleMessage(message, lineNumber, sourceID);
		if (mWrapper != null) {
			mWrapper.onConsoleMessage(message, lineNumber, sourceID);
		}
	}

	@Override
	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
		if (mWrapper != null) {
			return mWrapper.onConsoleMessage(consoleMessage);
		}
		return super.onConsoleMessage(consoleMessage);
	}

	@Override
	public Bitmap getDefaultVideoPoster() {
		if (mWrapper != null) {
			return mWrapper.getDefaultVideoPoster();
		}
		return super.getDefaultVideoPoster();
	}

	@Override
	public View getVideoLoadingProgressView() {
		if (mWrapper != null) {
			return mWrapper.getVideoLoadingProgressView();
		}
		return super.getVideoLoadingProgressView();
	}

	@Override
	public void getVisitedHistory(ValueCallback<String[]> callback) {
		super.getVisitedHistory(callback);
		if (mWrapper != null) {
			mWrapper.getVisitedHistory(callback);
		}
	}

	@Override
	public void onExceededDatabaseQuota(String url, String databaseIdentifier,
			long quota, long estimatedDatabaseSize, long totalQuota,
			QuotaUpdater quotaUpdater) {
		super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
				estimatedDatabaseSize, totalQuota, quotaUpdater);
		if (mWrapper != null) {
			mWrapper.onExceededDatabaseQuota(url, databaseIdentifier, quota,
					estimatedDatabaseSize, totalQuota, quotaUpdater);
		}
	}

	@Override
	public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
			QuotaUpdater quotaUpdater) {
		super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
		if (mWrapper != null) {
			mWrapper.onReachedMaxAppCacheSize(requiredStorage, quota,
					quotaUpdater);
		}
	}

}
