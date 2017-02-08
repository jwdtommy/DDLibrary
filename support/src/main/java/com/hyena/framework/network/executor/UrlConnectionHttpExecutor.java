package com.hyena.framework.network.executor;

import android.text.TextUtils;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.debug.DebugUtils;
import com.hyena.framework.network.HttpError;
import com.hyena.framework.network.HttpExecutor;
import com.hyena.framework.network.HttpListener;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.network.NetworkSensor;
import com.hyena.framework.network.utils.HttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by yangzc on 16/7/12.
 */
public class UrlConnectionHttpExecutor implements HttpExecutor {

    private static final String TAG = "UrlConnectionHttpExecutor";
    private static final String BOUNDARY = "---------------------------7d4a6d158c9";

    private static final int DEFAULT_TIMEOUT = 30;

    @Override
    public HttpResult doGet(String url, HttpRequestParams params,
                            HttpListener httpListener) {
        return doExecute(true, url, params, httpListener);
    }

    @Override
    public HttpResult doPost(String url, HttpRequestParams params,
                             HttpListener httpListener) {
        return doExecute(false, url, params, httpListener);
    }

    private HttpResult doExecute(boolean isGet, String url, final HttpRequestParams params,
                                 HttpListener listener) {

        debug("method: " + (isGet ? "Get" : "Post") + ", execute :" + url);
        int timeout = DEFAULT_TIMEOUT;
        //如果外部没有设置超时时间，则使用默认30s超时
        if(params != null && params.mTimeout > 0)
            timeout = params.mTimeout;

        HttpResult result = new HttpResult();
        result.mHttpListener = listener;

        //检查网络
        NetworkSensor networkSensor = NetworkProvider.getNetworkProvider().getNetworkSensor();
        if(networkSensor == null || !networkSensor.isNetworkAvailable()){
            result.mErrorCode = HttpError.ERROR_NO_AVAILABLE_NETWORK;
            if(listener != null)
                try {
                    listener.onError(HttpError.ERROR_NO_AVAILABLE_NETWORK);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            return result;
        }

        if(TextUtils.isEmpty(url)){
            if(listener != null)
                try {
                    listener.onError(HttpError.ERROR_URL_EMPTY);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            result.mErrorCode = HttpError.ERROR_URL_EMPTY;
            return result;
        }

        URLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            if(listener != null && !listener.onReady(url)){
                result.mErrorCode = HttpError.ERROR_CANCEL_READY;
                return result;
            }
            //重新构筑URL
            String new_url = networkSensor.rebuildUrl(url);
            if(!TextUtils.isEmpty(new_url)){
                url = new_url;
            }
            long start = System.currentTimeMillis();
            result.mUrl = url;//同步请求的URL

            //添加默认代理
            ProxyHost proxy = networkSensor.getProxyHost(url, params.isProxy);

            ByteArrayOutputStream baos = null;
            URL urlObj;
            if(isGet){//Get请求
                //重新定义请求字符串
                if(params != null && params.mParams != null)
                    url = HttpUtils.encodeUrl(url, params.mParams);
                urlObj = new URL(url);
                conn = openConnection(isGet, urlObj, proxy);
            } else {
                if (params != null) {
                    if (params.mOsHandler != null) {
                        if (params.mParams != null) {
                            url = HttpUtils.encodeUrl(url, params.mParams);
                        }
                        urlObj = new URL(url);
                        conn = openConnection(isGet, urlObj, proxy);
                        conn.setRequestProperty("Content-Type", "application/json");

                        baos = new ByteArrayOutputStream((int) params.mOsHandler.getLength());
                        params.mOsHandler.writeTo(baos);
                    } else if (params.mByteFileMap != null) {
                        urlObj = new URL(url);
                        conn = openConnection(isGet, urlObj, proxy);
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                        //write data
                        baos = new ByteArrayOutputStream();
                        if (params.mParams != null) {
                            for (int i = 0; i < params.mParams.size(); i++) {
                                KeyValuePair keyValue = params.mParams.get(i);
                                baos.write(("--" + BOUNDARY + "\r\n").getBytes());
                                baos.write(("Content-Disposition: form-data; name=\"" + keyValue.getKey() + "\"\r\n").getBytes());
                                baos.write("\r\n".getBytes());
                                baos.write((HttpUtils.encodeUrl(keyValue.getValue() + "\r\n").getBytes()));
                            }
                        }

                        Set<String> keySet = params.mByteFileMap.keySet();
                        Iterator<String> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            String name = iterator.next();
                            ByteFile file = params.mByteFileMap.get(name);
                            baos.write(("--" + BOUNDARY + "\r\n").getBytes());
                            baos.write(("Content-Disposition: form-data; name=\"" + name
                                    + "\"; filename=\"" + HttpUtils.encodeUrl(file.mFileName) + "\"\r\n").getBytes());
                            baos.write(("Content-Type: " + file.mMimeType + "\r\n").getBytes());
                            baos.write("\r\n".getBytes());
                            baos.write(file.mBytes);
                            baos.write("\r\n".getBytes());
                        }
                    } else if (params.mParams != null){
                        urlObj = new URL(url);
                        byte data[] = HttpUtils.encodeUrl(params.mParams).getBytes();
                        conn = openConnection(isGet, urlObj, proxy);
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        //write data
                        baos = new ByteArrayOutputStream();
                        baos.write(data);
                    }
                } else {
                    urlObj = new URL(url);
                    conn = openConnection(isGet, urlObj, proxy);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                }
            }

            long startPos = 0;
            if(params != null && params.mStartPos > 0){
                startPos = params.mStartPos;
                conn.addRequestProperty("RANGE", "bytes=" + startPos + "-");
                result.mStartPos = startPos;
            }

            //添加默认头
            List<KeyValuePair> commonHeaders = networkSensor.getCommonHeaders(url, params.isProxy);
            if(commonHeaders != null){
                for(KeyValuePair pair: commonHeaders){
                    conn.addRequestProperty(pair.getKey(), pair.getValue());
                }
            }
            //添加自定义头
            if(params != null && params.mHeader != null){
                Iterator<String> iterator = params.mHeader.keySet().iterator();
                while(iterator.hasNext()){
                    String name = iterator.next();
                    String value = params.mHeader.get(name);
                    conn.addRequestProperty(name, value);
                }
            }

            //TODO https support
            //other setting
            conn.setConnectTimeout(timeout * 1000);
            conn.setReadTimeout(timeout * 1000);

            start = System.currentTimeMillis();

            if (baos != null) {
                conn.setRequestProperty("Content-Length", baos.toByteArray().length + "");
                os = conn.getOutputStream();
                os.write(baos.toByteArray());
                os.close();
            }

            is = conn.getInputStream();

            int statusCode = HttpURLConnection.HTTP_OK;
            if (conn instanceof HttpURLConnection) {
                statusCode = ((HttpURLConnection)conn).getResponseCode();
            } else if (conn instanceof HttpsURLConnection) {
                statusCode = ((HttpsURLConnection)conn).getResponseCode();
            }

            String encoder = conn.getHeaderField("Transfer-Encoding");
            if(encoder != null){
                if("chunked".equalsIgnoreCase(encoder)){
                    result.mIsTrunked = true;
                }
            }

            if(listener != null && listener.onResponse(is,
                    os,
                    statusCode,
                    conn.getContentType(),
                    conn.getContentEncoding(),
                    conn.getContentLength(),
                    false,
                    result.mIsTrunked)) {
                result.mErrorCode = HttpError.ERROR_CANCEL_RESPONSE;
                return result;
            }

            result.mReqTs = System.currentTimeMillis() - start;//请求响应时间
            result.mStatusCode = statusCode;//同步状态码

            debug("statusCode :" + statusCode);
            if(statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_PARTIAL) {
                if(LogUtil.isDebug()) {
                    Map<String, List<String>> headers = conn.getHeaderFields();
                    Iterator<String> iterator = headers.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        List<String> value = headers.get(key);
                        if (value != null) {
                            for (int i = 0; i < value.size(); i++) {
                                debug("header name: " + key + ", value: " + value.get(i));
                            }
                        }
                    }
                }

                long contentLength = conn.getContentLength();
                result.mContentLength = contentLength;//同步网络数据长度

                boolean isGzip = "gzip".equals(conn.getHeaderField("Content-Encoding"));
                result.mIsGzip = isGzip;
                debug("contentLength : " + contentLength + ", trunked : " + result.mIsTrunked + ", gzip: " + result.mIsGzip);

				/* 通知开始下载 */
                if(listener != null){
                    if(!listener.onStart(startPos, contentLength)){
                        //开始时Cancel掉Http请求
                        result.mErrorCode = HttpError.ERROR_CANCEL_BEGIN;
                        return result;
                    }
                }

                if(isGzip) {
                    is = new GZIPInputStream(is);
                }

                int len = -1;
                //控制buffer长度
                int bufferSize = 1024 * 10;
                if(params != null && params.mBufferSize > 0){
                    bufferSize = params.mBufferSize;
                }
                byte buf[] = new byte[bufferSize];
                start = System.currentTimeMillis();
                while((len = is.read(buf, 0, bufferSize)) > 0){
                    if(listener != null){
                        if(!listener.onAdvance(buf, 0, len)){
                            //正在下载过程中Cancel掉Http请求
                            result.mErrorCode = HttpError.ERROR_CANCEL_ADVANCE;
                            break;
                        }
                    }
                    if(networkSensor != null){
                        networkSensor.updateFlowRate(len);
                    }
                }
                result.mReadTs = System.currentTimeMillis() - start;//读取时间
                if(listener != null && result.isSuccess()){
                    listener.onCompleted();
                }
            }else{
                result.mErrorCode = HttpError.ERROR_STATUS_CODE;
                if(listener != null)
                    listener.onError(statusCode);
            }
        } catch (FileNotFoundException e) {
            if(listener != null)
                try {
                    listener.onError(HttpError.ERROR_UNKNOWN);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            result.mErrorCode = HttpError.ERROR_UNKNOWN;
            LogUtil.e("", e);
        } catch (IOException e) {
            if(listener != null)
                try {
                    listener.onError(HttpError.ERROR_UNKNOWN);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            result.mErrorCode = HttpError.ERROR_UNKNOWN;
            LogUtil.e("", e);
        } catch (Throwable e){
            if(listener != null)
                try {
                    listener.onError(HttpError.ERROR_UNKNOWN);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            result.mErrorCode = HttpError.ERROR_UNKNOWN;
            LogUtil.e("", e);
        } finally {
            //关闭流
            if(is != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
                try {
                    is.close();
                } catch (Exception e) {
                    LogUtil.e("", e);
                }
            }
            if(os != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
                try {
                    os.close();
                } catch (Exception e) {
                    LogUtil.e("", e);
                }
            }
            //关闭请求
            if(conn != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
                try{
                    if (conn instanceof HttpURLConnection) {
                        ((HttpURLConnection)conn).disconnect();
                    } else if(conn instanceof HttpsURLConnection) {
                        ((HttpsURLConnection)conn).disconnect();
                    }
                }catch(Exception e){
                    LogUtil.e("", e);
                }
            }
            //通知结束,开始销毁
            if(listener != null && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE){
                try {
                    listener.onRelease();
                } catch (Throwable e) {
                    LogUtil.e("", e);
                }
            }
        }
        debug("isCancel : " + result.isCanceled() + ", reason :" + result.getCancelReason());
        return result;
    }

    /**
     * 打开连接
     * @param isGet
     * @param url
     * @param proxyHost
     * @return
     * @throws IOException
     */
    private URLConnection openConnection(boolean isGet, URL url, ProxyHost proxyHost)
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
        if (url == null) {
            throw new IOException("url is empty");
        }

        URLConnection conn;
        if (proxyHost != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    proxyHost.mHost, proxyHost.mPort));
            conn = url.openConnection(proxy);
        } else {
            conn = url.openConnection();
        }
        if (conn instanceof HttpsURLConnection) {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { new EasyX509TrustManager() }, null);
            ((HttpsURLConnection)conn).setSSLSocketFactory(context.getSocketFactory());
            ((HttpsURLConnection)conn).setRequestMethod(isGet? "GET" : "POST");
        } else if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection)conn).setRequestMethod(isGet? "GET" : "POST");
        }
        conn.setDoInput(true);
        if (!isGet) {
            conn.setDoOutput(true);
        }
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept-Encoding", "identity");
        return conn;
    }

    private void debug(String str){
        if(FrameworkConfig.getConfig().isDebug()){
            LogUtil.v(TAG, str);
        }
        DebugUtils.debugTxt(str);
    }

}
