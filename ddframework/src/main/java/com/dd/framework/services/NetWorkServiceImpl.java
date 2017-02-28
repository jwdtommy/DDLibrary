package com.dd.framework.services;

import android.content.Context;
import android.util.Log;
import com.dd.framework.base.BaseApp;
import com.dd.framework.base.BaseResult;
import com.dd.framework.utils.CommonUtils;
import java.io.File;
import java.io.IOException;
import okhttp3.Cache;
import okhttp3.CacheControl;
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
 * Created by J.Tommy on 17/2/10.
 * framework中的网络层负责：
 * 1.缓存的处理
 * 2.log的过滤
 * app中的网络层负责：
 * 1.公共请求
 * 2公共返回值的处理
 */

public abstract class NetWorkServiceImpl<T> implements NetWorkService {
	private static final String TAG = "HttpHelper";
	private final String CACHE_NAME = "Cache";
	private final int CACHE_SIZE = 10 * 1024 * 1024;
	private final int MAX_AGE = 60;//在线缓存一分钟
	private final int MAX_STALE = 60 * 60 * 24 * 30;     //离线缓存一个月
	private Retrofit retrofit;
	private T apiBox;
	private Class<T> apiBoxClass;


	public NetWorkServiceImpl(Class<T> apiBoxClass) {
		this.apiBoxClass = apiBoxClass;
	}

	@Override
	public T getAPIBox() {
		if (apiBox == null) {
			retrofit = new Retrofit.Builder()
					.client(getCacheOkHttpClient(BaseApp.getInstance()))
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.baseUrl(configBaseUrl())
					.build();
			apiBox = retrofit.create(apiBoxClass);
		}
		return apiBox;
	}

	private OkHttpClient getCacheOkHttpClient(final Context context) {
		final File baseDir = context.getCacheDir();
		final File cacheDir = new File(baseDir, CACHE_NAME);
		final Cache cache = new Cache(cacheDir, CACHE_SIZE);   //缓存可用大小为10M
		return new OkHttpClient.Builder()
				.addInterceptor(configPublicParamsInterceptor())
				.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
				.addInterceptor(LOG)
				.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
				.cache(cache)
				.build();
	}

	public abstract String configBaseUrl();

	public abstract Interceptor configPublicParamsInterceptor();

	private void toSubscribe(Observable o, Subscriber s) {
		o.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(s);
	}

	public void execute(Subscriber subscriber, Observable observable) {
		observable = observable.map(new ApiResultInterceptor());
		toSubscribe(observable, subscriber);
	}


	/**
	 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
	 *
	 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
	 */
	private class ApiResultInterceptor<T> implements Func1<BaseResult<T>, T> {
		@Override
		public T call(BaseResult<T> baseResult) {
			baseResult.transfrom();
			if (baseResult.error_code < 0) {
				throw new ApiException(baseResult.error_code);
			}
			return baseResult.data;
		}
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
	 * 缓存拦截器
	 */
	Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			if (!CommonUtils.isNetworkConnected(BaseApp.getInstance())) {
				request = request.newBuilder()
						.cacheControl(CacheControl.FORCE_CACHE)
						.build();
			}

			Response originalResponse = chain.proceed(request);
			if (CommonUtils.isNetworkConnected(BaseApp.getInstance())) {
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
}
