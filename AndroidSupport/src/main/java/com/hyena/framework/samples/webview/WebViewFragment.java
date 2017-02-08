package com.hyena.framework.samples.webview;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.hyena.framework.app.fragment.BaseWebFragment;
import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.app.widget.WebChromeClientWrapper;
import com.hyena.framework.app.widget.WebViewClientWrapper;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.FileUtils;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.MathUtils;

import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * Created by yangzc on 16/5/30.
 */
public class WebViewFragment extends BaseWebFragment {

    private HybirdWebView mWebView;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        mWebView = new HybirdWebView(getActivity());
        return mWebView;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        loadWebView();
        setWebView(mWebView);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.setWebViewClient(new WebViewClientWrapper(mWebViewClient) {

            private boolean isShouldOverride(String url){
                return url != null && url.startsWith(HybirdWebView.ACTION_PREX + "image_load");
            }

            private RequestInfo getRealUrl(String url) {
                try {
                    String body = url.replace(HybirdWebView.ACTION_PREX, "");
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
                        return new RequestInfo(method, valueMap);
                    } else {
                        String methodName = url.replace("hybird://method/", "");
                        return new RequestInfo(methodName, null);
                    }
                } catch (Exception e) {}
                return null;
            }

            class RequestInfo {
                public String method;
                public Hashtable<String, String> params;

                public RequestInfo(String method, Hashtable<String, String> params){
                    this.method = method;
                    this.params = params;
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && isShouldOverride(url)) {
                    LogUtil.v("yangzc", "onLoadResource -- > " + url);
                    mWebView.handleUrlLoading(url);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && isShouldOverride(url)) {
                    LogUtil.v("yangzc", "shouldInterceptRequest11 -- > " + url);
                    RequestInfo requestInfo = getRealUrl(url);
                    if (requestInfo != null) {
                        String rawUrl = requestInfo.params.get("url");
                        int width = MathUtils.valueOfInt(requestInfo.params.get("w"));
                        int height = MathUtils.valueOfInt(requestInfo.params.get("h"));

                        Bitmap bitmap = ImageFetcher.getImageFetcher().loadImageSync(rawUrl, width, height, 0);
                        if (bitmap != null) {
                            File file = ImageFetcher.getImageFetcher().getCacheFilePath(rawUrl);
                            try {
                                return new WebResourceResponse("image/png", "utf-8", new FileInputStream(file));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isShouldOverride(request.getUrl().toString())) {
                    LogUtil.v("yangzc", "shouldInterceptRequest21 -- > " + request.getUrl().toString());

                    RequestInfo requestInfo = getRealUrl(request.getUrl().toString());
                    if (requestInfo != null) {
                        String rawUrl = requestInfo.params.get("url");
                        int width = MathUtils.valueOfInt(requestInfo.params.get("w"));
                        int height = MathUtils.valueOfInt(requestInfo.params.get("h"));

                        Bitmap bitmap = ImageFetcher.getImageFetcher().loadImageSync(rawUrl, width, height, 0);
                        if (bitmap != null) {
                            File file = ImageFetcher.getImageFetcher().getCacheFilePath(rawUrl);
                            try {
                                return new WebResourceResponse("image/png", "utf-8", new FileInputStream(file));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClientWrapper(mWebChromeClient) {
        });
    }

    @Override
    public void onPageFinished() {
        super.onPageFinished();
        try {
            InputStream is = BaseApp.getAppContext().getResources().getAssets().open("js/support_inject.js");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileUtils.copyStream(is, baos);
            is.close();
            String data = new String(baos.toByteArray()).replaceAll("[\\t\\n\\r]", "");
            LogUtil.v("yangzc", data);
            mWebView.loadUrl("javascript:" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean onCallMethodImpl(String methodName, Hashtable paramsMap) {
        if ("image_load".equalsIgnoreCase(methodName)) {
            try {
                String url = URLDecoder.decode((String) paramsMap.get("url"), "utf-8");
                LogUtil.v("yangzc", "resource: " + url);
                ImageFetcher.getImageFetcher().loadImage(url, url, new ImageFetcher.ImageFetcherListener() {
                    @Override
                    public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
                        if (bitmap != null) {
                            File file = ImageFetcher.getImageFetcher().getCacheFilePath(imageUrl);
                            runJs("showImage", imageUrl, Uri.fromFile(file).toString());
                            LogUtil.v("yangzc", "onLoadComplete --> " + Uri.fromFile(file).toString());
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        } else if ("image_show".equalsIgnoreCase(methodName)) {
            try {
                int x = MathUtils.valueOfInt((String) paramsMap.get("l"));
                int y = MathUtils.valueOfInt((String) paramsMap.get("t"));
                int w = MathUtils.valueOfInt((String) paramsMap.get("w"));
                int h = MathUtils.valueOfInt((String) paramsMap.get("h"));

                String url = URLDecoder.decode((String) paramsMap.get("url"), "utf-8");
                Rect rect = new Rect(x, y, x + w, y + h);
                getUIFragmentHelper().showPicture(rect, url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return super.onCallMethodImpl(methodName, paramsMap);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
    }

    private void loadWebView() {
        try {
            mWebView.loadUrl("file:///android_asset/newest.html");
//            mWebView.loadUrl("http://www.sina.com.cn");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
