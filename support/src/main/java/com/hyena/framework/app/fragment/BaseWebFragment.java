/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app.fragment;

import java.util.Hashtable;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.app.widget.HybirdWebView.WebViewActionListener;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * Web页基础类
 * @author yangzc on 15/8/20.
 */
@SuppressWarnings("deprecation")
public abstract class BaseWebFragment<T extends BaseUIFragmentHelper> extends BaseUIFragment<T> {

    private HybirdWebView mWebView;
    //加载页面的时候是否显示loading
    private boolean mShowLoadingWhenLoadPage = true;
    //js接管页面返回键
    private boolean mJsHandleBack = false;

    //剪切板
	private ClipboardManager mClipboardManager;

	@Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void finish() {
    	if(mJsHandleBack){
            runJs("onBackPressed");
        }else {
            super.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mJsHandleBack){
            runJs("onBackPressed");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 开始加载页面
     */
    public void onPageStarted() {
    	
    }
    
    /**
     * 页面加载完成
     */
    public void onPageFinished() {
    	
    }
    
    public void onError(int errorCode, String description, String failingUrl){};

    /**
     * 初始化WebView
     * @param webView
     */
    @SuppressLint("SetJavaScriptEnabled")
	public void setWebView(HybirdWebView webView) {
        this.mWebView = webView;
        
        mWebView.setActionListener(mActionListener);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
//        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }
    
    private WebViewActionListener mActionListener = new WebViewActionListener() {
		
		@Override
		public void onSizeChange(int width, int height) {
			onWebViewSizeChange(width, height);
		}
		
		@Override
		public void onDomReady() {
            BaseWebFragment.this.onDomReady();
		}
		
		@Override
		public void onCallMethod(String methodName,
				Hashtable<String, String> paramsMap) throws Exception {
			BaseWebFragment.this.onCallMethod(methodName, paramsMap);
		}
	};

    /**
     * 执行js
     * @param method
     * @param params
     */
    public void runJs(String method, String... params) {
        if(mWebView != null){
        	mWebView.runJs(method, params);
        }
    }

    /**
     * 是否需要显示loading
     * @param showLoading
     */
    public void shouldShowLoadingWhenLoadPage(boolean showLoading){
        this.mShowLoadingWhenLoadPage = showLoading;
    }

    public WebChromeClient mWebChromeClient = new WebChromeClient() {

    };

    public WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            BaseWebFragment.this.onPageStarted();
            if(url != null && !url.startsWith(HybirdWebView.ACTION_PREX) && mShowLoadingWhenLoadPage) {
                getLoadingView().showLoading();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            BaseWebFragment.this.onPageFinished();
            showContent();
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            onError(errorCode, description, failingUrl);
        }

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
    };

    /**
     * Web页请求方法
     * @param methodName
     * @param paramsMap
     * @return
     */
    protected boolean onCallMethodImpl(String methodName, Hashtable<String, String> paramsMap){
        return false;
    }
    
    /**
     * WebView大小改变
     * @param width
     * @param height
     */
    public void onWebViewSizeChange(int width, int height){
    	
    }

    /**
     * dom加载完毕
     */
    public void onDomReady() {}

    /**
     * Web页面请求方法调用
     * @param methodName
     * @param paramsMap
     */
	private void onCallMethod(String methodName, Hashtable<String, String> paramsMap) throws Exception {
        if(onCallMethodImpl(methodName, paramsMap)){
           return;
        }
        if("exit".equals(methodName)){//退出场景
            finish();
        } else if("setTitle".equals(methodName)){//设置标题
            String title = paramsMap.get("title");
            if(!TextUtils.isEmpty(title)){
                getTitleBar().setTitleVisible(true);
                getTitleBar().setTitle(title);
            }else{
                getTitleBar().setTitleVisible(false);
            }
        } else if("showLoading".equals(methodName)) {
            getLoadingView().showLoading();
        } else if("showEmpty".equals(methodName)) {
            getEmptyView().showEmpty("", paramsMap.get("hint"));
        } else if("showContent".equals(methodName)) {
            showContent();
        } else if("showLoadingWhenLoadPage".equals(methodName)){//加载页面时是否需要显示loading
            mShowLoadingWhenLoadPage = "1".equals(paramsMap.get("isShow")) ? true : false;
        } else if("handleBack".equals(methodName)){
            this.mJsHandleBack = "1".equals(paramsMap.get("handleBack")) ? true : false;
        } else if("cmdQueue".equals(methodName)) {
            String cmdList = paramsMap.get("cmdQueue");
            if(!TextUtils.isEmpty(cmdList)){
                JSONArray array = new JSONArray(cmdList);
                for (int i = 0; i < array.length(); i++) {
                    mWebView.handleUrlLoading(array.getString(i));
                }
            }
        } else if("copy2Clipboard".equals(methodName)) {
            String content = paramsMap.get("content");
            if(!TextUtils.isEmpty(content)){
                mClipboardManager.setText(content);
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(getActivity(), "成功复制到粘贴板");
                    }
                });
            }
        }
    }
}
