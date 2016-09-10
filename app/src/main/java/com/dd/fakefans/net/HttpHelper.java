package com.dd.fakefans.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.entry.base.MeituInfo;
import com.dd.fakefans.entry.base.ShowApiResult;
import com.dd.fakefans.base.App;
import com.dd.fakefans.utils.CommonUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by adong on 16/4/19.
 */
public class HttpHelper {
    private static final String TAG = "HttpHelper";


    private static final String SHOW_API_APP_ID = "760";
    private static final String SHOW_API_APP_SIGN = "8f62887a9b1f4124afb8076bbaf16543";
    private static final String PARAMS_SHOW_API_APP_ID = "showapi_appid";
    private static final String PARAMS_SHOW_API_APP_SIGN = "showapi_sign";

    public static final String BASE_URL_SHOW_API = "http://route.showapi.com/";


    private final String CACHE_NAME = "Cache";
    private final int CACHE_SIZE = 10 * 1024 * 1024;

    private final int MAX_AGE = 60;//在线缓存一分钟
    private final int MAX_STALE = 60 * 60 * 24 * 30;     //离线缓存一个月

    private static final int DEFAULT_TIMEOUT = 5;//http超时时间


    private Retrofit retrofit;
    private APIService apiService;

    private static final class Singlton {
        public static final HttpHelper instatnce = new HttpHelper();
    }

    public static HttpHelper getInstance() {
        return Singlton.instatnce;
    }

    private HttpHelper() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(getCacheOkHttpClient(App.getInstance()))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL_SHOW_API)
                .build();
        apiService = retrofit.create(APIService.class);
    }


    private OkHttpClient getCacheOkHttpClient(final Context context) {
        final File baseDir = context.getCacheDir();
        final File cacheDir = new File(baseDir, CACHE_NAME);
        final Cache cache = new Cache(cacheDir, CACHE_SIZE);   //缓存可用大小为10M
        return new OkHttpClient.Builder()
                .addInterceptor(PUBLIC_PARAMS)
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(LOG)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();
    }

    /**
     * log拦截器
     */
    private final Interceptor LOG = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.i(TAG, "url=" + request.url());
            Log.i(TAG, "header=" + request.headers());
            Log.i(TAG, "connecion" + chain.connection());
            Response response = chain.proceed(request);
            return response;
        }
    };

    /**
     * 公共参数的拦截器
     */
    Interceptor PUBLIC_PARAMS = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            // 添加新的参数
            HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
                    .addQueryParameter(PARAMS_SHOW_API_APP_ID, SHOW_API_APP_ID)
                    .addQueryParameter(PARAMS_SHOW_API_APP_SIGN, SHOW_API_APP_SIGN);
            // 新的请求
            Request newRequest = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(authorizedUrlBuilder.build())
                    .build();
            return chain.proceed(newRequest);
        }
    };

    /**
     * 缓存拦截器
     */
    Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!CommonUtils.isNetworkConnected(App.getInstance())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response originalResponse = chain.proceed(request);
            if (CommonUtils.isNetworkConnected(App.getInstance())) {
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + MAX_AGE)
                        .build();

            } else {
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE)
                        .build();
            }
        }
    };

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<ShowApiResult<T>, T> {

        @Override
        public T call(ShowApiResult<T> showApiResult) {
            if (TextUtils.isEmpty(showApiResult.getShowapi_res_code()) || !showApiResult.getShowapi_res_code().equals("0")) {
                throw new ApiException(showApiResult.getShowapi_res_code());//error
            }
            return showApiResult.getShowapi_res_body();
        }
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public void getNews(Subscriber<BuDeJieInfo> subscriber, String type, String page) {
        Observable observable = apiService.getNews(type, "", page)
                .map(new HttpResultFunc<BuDeJieInfo>());
        toSubscribe(observable, subscriber);
    }
    public void getGirls(Subscriber<MeituInfo> subscriber, String type, String page) {
        Observable observable = apiService.getGirls(type, page)
                .map(new HttpResultFunc<MeituInfo>());
        toSubscribe(observable, subscriber);
    }
}
