package com.dd.news.services.net;

import android.text.TextUtils;
import com.dd.framework.services.NetWorkServiceImpl;
import com.dd.news.entry.base.ShowApiResult;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.functions.Func1;
/**
 * Created by J.Tommy on 17/2/10.
 * 遵循一个域名对应一个ApiService的原则
 */
public class APIServiceImpl extends NetWorkServiceImpl<APIBox> {
	private static final String SHOW_API_APP_ID = "760";
	private static final String SHOW_API_APP_SIGN = "8f62887a9b1f4124afb8076bbaf16543";
	private static final String PARAMS_SHOW_API_APP_ID = "showapi_appid";
	private static final String PARAMS_SHOW_API_APP_SIGN = "showapi_sign";
	private static final String BASE_URL_SHOW_API = "http://route.showapi.com/";

	public APIServiceImpl() {
		super(APIBox.class);
	}

	@Override
	public String configBaseUrl() {
		return BASE_URL_SHOW_API;
	}

	@Override
	public Interceptor configPublicParamsInterceptor() {
		return PUBLIC_PARAMS;
	}

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

//	/**
//	 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
//	 *
//	 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
//	 */
//	private class ApiResultInterceptor<T> implements Func1<ShowApiResult<T>, T> {
//		@Override
//		public T call(ShowApiResult<T> showApiResult) {
//			if (TextUtils.isEmpty(showApiResult.getShowapi_res_code()) || !showApiResult.getShowapi_res_code().equals("0")) {
//				throw new ApiException(showApiResult.getShowapi_res_code());//error
//			}
//			return showApiResult.getShowapi_res_body();
//		}
//	}
//
//	@Override
//	public Func1 configApiResultInterceptor() {
//		return new ApiResultInterceptor();
//	}
}
