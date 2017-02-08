/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.MathUtils;

import org.apache.http.protocol.HTTP;

import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * HybirdWebView </p>
 * 
 * @author yangzc
 *
 */
public class HybirdWebView extends WebView {

	public static final String TAG = "HybirdWebView";
	
	public static final String ACTION_PREX = "hybird://method/";
	
	/**
	 * WebView事件回调
	 * 
	 * @author yangzc
	 *
	 */
	public static interface WebViewActionListener {

		/**
		 * Web页面请求方法调用
		 * 
		 * @param methodName
		 * @param paramsMap
		 */
		public void onCallMethod(String methodName,
				Hashtable<String, String> paramsMap) throws Exception;

		/**
		 * 窗口大小发生变化
		 * 
		 * @param width
		 * @param height
		 */
		public void onSizeChange(int width, int height);

		/**
		 * Dom准备完成
		 */
		public void onDomReady();
	}

	private WebChromeClientWrapper mWebChromeClientWrapper;
	private WebViewClientWrapper mWebViewClientWrapper;
	private WebViewActionListener mActionListener;

	public HybirdWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public HybirdWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HybirdWebView(Context context) {
		super(context);
		init();
	}

	/**
	 * init params
	 */
	private void init() {
		mWebChromeClientWrapper = new WebChromeClientWrapper() {
			
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				if (consoleMessage == null) {
					return super.onConsoleMessage(consoleMessage);
				}
				//内置方法
				if ("domready".equals(consoleMessage.message())) {
					LogUtil.v(TAG, "on dom ready!!!");
					getWebViewSize();
					if (mActionListener != null) {
						mActionListener.onDomReady();
					}
				}
				
				if (consoleMessage.message() != null 
						&& consoleMessage.message().startsWith(ACTION_PREX)) {
					handleUrlLoading(consoleMessage.message());
					return true;
				}

				return super.onConsoleMessage(consoleMessage);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message
					, String defaultValue, JsPromptResult result) {
				if (message != null && message.startsWith(ACTION_PREX)) {
					handleUrlLoading(message);
					result.confirm();
					return true;
				}
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				onGetTitle(title);
			}

		};
		
		mWebViewClientWrapper = new WebViewClientWrapper() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				boolean result = super.shouldOverrideUrlLoading(view, url);
				if (!result) {
					if (url != null && url.startsWith(ACTION_PREX)) {
                        LogUtil.v("yangzc", "handle --> " + url);
						handleUrlLoading(url);
						return true;
					}
				}
				return result;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(!getSettings().getLoadsImagesAutomatically()) {
			        getSettings().setLoadsImagesAutomatically(true);
			    }
				getWebViewSize();
			}

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }
        };

		super.setWebChromeClient(mWebChromeClientWrapper);
		super.setWebViewClient(mWebViewClientWrapper);
		if(Build.VERSION.SDK_INT >= 19) {
	        getSettings().setLoadsImagesAutomatically(true);
	    } else {
	        getSettings().setLoadsImagesAutomatically(false);
	    }
	}

	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
	}
	
	@Override
	public void loadDataWithBaseURL(String baseUrl, String data,
			String mimeType, String encoding, String historyUrl) {
		if (!TextUtils.isEmpty(data) && data.contains("</body>")) {
			data = data.replace("</body>", "<script>console.log('domready')</script></body>");
		}
		super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
	}
	
	/**
	 * 设置Action监听器
	 * 
	 * @param listener
	 */
	public void setActionListener(WebViewActionListener listener) {
		this.mActionListener = listener;
	}

	@Override
	public void setWebChromeClient(WebChromeClient client) {
		if (mWebChromeClientWrapper != null) {
			mWebChromeClientWrapper.setWebChromeClient(client);
		}
	}

	@Override
	public void setWebViewClient(WebViewClient client) {
		if (mWebViewClientWrapper != null) {
			mWebViewClientWrapper.setWebViewClient(client);
		}
	}

	public void onGetTitle(String title){

	}
	
	/**
	 * get size of the webview
	 */
	public void getWebViewSize() {
		StringBuffer js = new StringBuffer();
		js.append("var w=document.body.scrollWidth;"
				+ "var h=document.body.scrollHeight;"
				+ "console.log(\"hybird://method/sizeChange?w=\" + w + \"&h=\" + h)");
		loadUrl("javascript:" + js.toString());
	}

	/**
	 * call javascript
	 * 
	 * @param method
	 * @param params
	 */
	public void runJs(String method, String... params) {
		StringBuffer jsBuffer = new StringBuffer();
		jsBuffer.append("javascript:");
		jsBuffer.append(method);
		jsBuffer.append("(");
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				if (i == 0) {
					jsBuffer.append("'" + params[i] + "'");
				} else {
					jsBuffer.append(",'" + params[i] + "'");
				}
			}
		}
		jsBuffer.append(")");
		loadUrl(jsBuffer.toString());
	}

	/**
	 * 处理HandlerURL
	 * 
	 * @param url
	 */
	public void handleUrlLoading(String url) {
		try {
			String body = url.replace(ACTION_PREX, "");
			if (body.indexOf("?") != -1) {
				String method = body.substring(0, body.indexOf("?"));
				String query = body.replace(method + "?", "");
				String paramsArray[] = query.split("&");
				Hashtable<String, String> valueMap = new Hashtable<String, String>();
				for (int i = 0; i < paramsArray.length; i++) {
					String params[] = paramsArray[i].split("=");
					String key = URLDecoder.decode(params[0], HTTP.UTF_8);
					String value = URLDecoder.decode(params[1], HTTP.UTF_8);
					valueMap.put(key, value);
				}
				onCallMethod(method, valueMap);
			} else {
				String methodName = url.replace("hybird://method/", "");
				onCallMethod(methodName, null);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 执行页面回调
	 * 
	 * @param methodName
	 * @param paramsMap
	 * @throws Exception
	 */
	private void onCallMethod(String methodName,
			Hashtable<String, String> paramsMap) throws Exception {
		if ("sizeChange".equals(methodName)) {
			String w = paramsMap.get("w");
			String h = paramsMap.get("h");
			LogUtil.v(TAG, "size change, width: " + w + ", height: " + h);
			if (mActionListener != null) {
				mActionListener.onSizeChange(MathUtils.valueOfInt(w),
						MathUtils.valueOfInt(h));
			}
			return;
		}
		if (mActionListener != null) {
			mActionListener.onCallMethod(methodName, paramsMap);
		}
	}
}
