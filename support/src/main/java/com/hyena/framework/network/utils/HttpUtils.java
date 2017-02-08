package com.hyena.framework.network.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.executor.SSLHttpClient;
import com.hyena.framework.network.listener.FileHttpListener;

import org.apache.http.HttpHost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class HttpUtils {

    /**
     * 抓取文件 并存储为指定文件名, 3.3.0
     *
     * @param fileUrl
     * @param fileName
     * @return
     */
    public static boolean storeFile(String fileUrl, final String fileName) {
        HttpProvider httpProvider = new HttpProvider();
        FileHttpListener httpListener = new FileHttpListener(fileName) {
            @Override
            public void onError(int statusCode) {
                super.onError(statusCode);
                File saveToFile = new File(fileName);
                if (saveToFile.exists())
                    saveToFile.delete();
            }
        };
        HttpResult result = httpProvider.doGet(fileUrl, 30, httpListener);
        if (result != null && result.isSuccess()) {
            return true;
        }
        return false;
    }

    /**
     * Create a thread-safe client. This client does not do redirecting, to
     * allow us to capture correct "error" codes.
     *
     * @return HttpClient
     */
    public static final DefaultHttpClient createHttpClient(int timeout) {
        final HttpParams httpParams = createHttpParams(timeout);
        return SSLHttpClient.getInstance(httpParams);
    }

    /**
     * Create the default HTTP protocol parameters.
     */
    private static final HttpParams createHttpParams(int timeOut) {
        final HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        HttpConnectionParams.setConnectionTimeout(params, timeOut * 1000);
        HttpConnectionParams.setSoTimeout(params, timeOut * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 10240);

        HttpConnectionParams.setTcpNoDelay(params, false);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(32));
        ConnManagerParams.setMaxTotalConnections(params, 256);
        HttpProtocolParams.setUseExpectContinue(params, false);//100-continue
//		HttpProtocolParams.setUserAgent(params, "android_" + FrameworkConfig.getConfig().getVersionName()  + ";" + FrameworkConfig.getConfig().getUserAgent());
        HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
        return params;
    }

    /**
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static HttpHost getProxy(Context context) {
        if (context == null)
            return null;

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || networkInfo.getExtraInfo() == null) {
            return null;
        }
        // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
        String info = networkInfo.getExtraInfo().toLowerCase(Locale.getDefault());
        // 先根据网络apn信息判断,并进行 proxy 自动补齐
        if (info != null) {
            if (info.startsWith("cmwap") || info.startsWith("uniwap")
                    || info.startsWith("3gwap")) {
                HttpHost proxy = new HttpHost("10.0.0.172", 80);
//				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                return proxy;
            } else if (info.startsWith("ctwap")) {
                HttpHost proxy = new HttpHost("10.0.0.200", 80);
//				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                return proxy;
            } else if (info.startsWith("cmnet") || info.startsWith("uninet")
                    || info.startsWith("ctnet") || info.startsWith("3gnet")) {
                return null;
            } // else fall through
        } // else fall through

        // 如果没有 apn 信息，则根据 proxy代理判断。
        // 由于android 4.2 对 "content://telephony/carriers/preferapn"
        // 读取进行了限制，我们通过系统接口获取。

        // 绝大部分情况下不会走到这里
        // 此两个方法是deprecated的，但在4.2下仍可用
        String defaultProxyHost = android.net.Proxy.getDefaultHost();
        int defaultProxyPort = android.net.Proxy.getDefaultPort();

        if (defaultProxyHost != null && defaultProxyHost.length() > 0) {
            /*
			 * 无法根据 proxy host 还原 apn 名字 这里不设置 mApn
			 */
            if ("10.0.0.172".equals(defaultProxyHost.trim())) {
                // 当前网络连接类型为cmwap || uniwap
                HttpHost proxy = new HttpHost("10.0.0.172", defaultProxyPort);
//				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                return proxy;
            } else if ("10.0.0.200".equals(defaultProxyHost.trim())) {
                HttpHost proxy = new HttpHost("10.0.0.200", 80);
//				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                return proxy;
            } else {
            }
        } else {
            // 其它网络都看作是net
        }
        return null;
    }

    public static String encodeUrl(List<KeyValuePair> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            KeyValuePair p = params.get(i);
            if (i > 0) {
                sb.append("&");
            }
            sb.append(encodeUrl(p.getKey())).append("=")
                    .append(encodeUrl(p.getValue()));
        }
        return sb.toString();
    }

    public static String encodeUrl(String url, List<KeyValuePair> params) {
        if (params == null || params.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        if (url.contains("?")) {
            sb.append(url).append("&");
        } else {
            sb.append(url).append("?");
        }
        for (int i = 0; i < params.size(); i++) {
            KeyValuePair p = params.get(i);
            if (i > 0) {
                sb.append("&");
            }
            sb.append(encodeUrl(p.getKey())).append("=")
                    .append(encodeUrl(p.getValue()));
        }
        return sb.toString();
    }

    public static String encodeUrl(String value) {
        String encoded;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            encoded = value;
        }
        StringBuilder sb = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                sb.append("%2A");
            } else if (focus == '+') {
                sb.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(focus);
            }
        }
        return sb.toString();
    }
}
