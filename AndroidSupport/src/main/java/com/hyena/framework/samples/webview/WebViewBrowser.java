package com.hyena.framework.samples.webview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hyena.framework.app.fragment.BaseWebFragment;
import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.samples.R;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.VersionUtils;

/**
 * Created by yangzc on 16/9/5.
 */
public class WebViewBrowser extends BaseWebFragment {

    private EditText mEditText;
    private HybirdWebView mWebview;
    private Button mBtnRefresh;
    private ProgressBar mPbProgress;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_webview_browser, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mEditText = (EditText) view.findViewById(R.id.et_url);
        mWebview = (HybirdWebView) view.findViewById(R.id.hwv_web);
        mPbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebview.reload();
            }
        });

        setWebView(mWebview);
        String userAgent = mWebview.getSettings().getUserAgentString();
        mWebview.getSettings().setUserAgentString(userAgent
                + " AppOS/android"
                + " AppFrom/knowBox"
                + " AppVersion/" + VersionUtils.getVersionCode(getActivity()));
        mWebview.setHorizontalScrollBarEnabled(false);
        mWebview.setVerticalScrollBarEnabled(false);

        mBtnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebview.loadUrl("http://" + mEditText.getText().toString());
                UIUtils.setInputMethodVisibility(getActivity(), mEditText, false);
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mPbProgress.setProgress(newProgress);
            }
        });
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mRefreshLayout.setRefreshing(false);
            }
        });
    }
}
