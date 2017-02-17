package com.dd.framework.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * Created by J.Tommy on 17/2/8.
 */

public class WebFragment extends BaseFragment {
	CustomWebView mCustomWebView;
	private String mUrl;

	@Override
	public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mUrl = getArguments().getString("url");
		mCustomWebView = new CustomWebView(getActivity());
		return mCustomWebView;
	}

	@Nullable
	@Override
	public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		init();
	}

	private void init() {
//		mCustomWebView.setActionListener(mActionListener);
//		mCustomWebView.setWebViewClient(mWebViewClient);
//		mCustomWebView.setWebChromeClient(mWebChromeClient);、
		//覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		mCustomWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				//返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		mCustomWebView.getSettings().setJavaScriptEnabled(true);
		mCustomWebView.getSettings().setUseWideViewPort(true);
		mCustomWebView.getSettings().setAppCacheEnabled(true);
		mCustomWebView.getSettings().setDatabaseEnabled(true);
		mCustomWebView.getSettings().setSupportZoom(true);
		mCustomWebView.getSettings().setDisplayZoomControls(true);
		//扩大比例的缩放
		mCustomWebView.getSettings().setUseWideViewPort(true);
//自适应屏幕
		mCustomWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		mCustomWebView.getSettings().setLoadWithOverviewMode(true);
		mCustomWebView.loadUrl(mUrl);
	}
}
