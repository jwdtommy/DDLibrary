/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认网络感应器
 *
 * @author yangzc
 */
public class DefaultNetworkSensor implements NetworkSensor {

    private static final String TAG = "DefaultNetworkSensor";

    public DefaultNetworkSensor() {
    }

    @Override
    public boolean isNetworkAvailable() {
        return isNetworkAvailable(BaseApp.getAppContext());
    }

    @Override
    public HttpExecutor.ProxyHost getProxyHost(String url, boolean isProxy) {
        return getProxy(url, isProxy);
    }

    @Override
    public List<KeyValuePair> getCommonHeaders(String url, boolean isProxy) {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        return headers;
    }

    @Override
    public void updateFlowRate(long len) {
    }

    @Override
    public ConnectivityManager getConnectivityManager(Context context) {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return mConnectivityManager;
    }

    @Override
    public String rebuildUrl(String url) {
        return url.replace(" ", "%20").replace("\"", "%22")
                .replace("#", "%23").replace("(", "%28")
                .replace(")", "%29").replace("+", "%2B").replace(",", "%2C")
                .replace(";", "%3B").replace("<", "%3C")
                .replace(">", "%3E").replace("@", "%40")
                .replace("\\", "%5C").replace("|", "%7C");
    }

    private ConnectivityManager mConnectivityManager;

    private boolean isNetworkAvailable(Context context) {
        if (context == null)
            return false;

        try {
            if (getConnectivityManager(context) == null) {
                LogUtil.d(TAG, "+++couldn't get connectivity manager");
            } else {
                NetworkInfo[] info = getConnectivityManager(context).getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            LogUtil.d(TAG, "+++network is available");
                            return true;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "+++network is not available");
        return false;
    }

    private HttpExecutor.ProxyHost getProxy(String url, boolean isProxy) {
        return null;
    }

    public void release() {
    }

}
