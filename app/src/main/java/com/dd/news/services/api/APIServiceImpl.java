package com.dd.news.services.api;

import com.dd.framework.services.network.NetWorkServiceImpl;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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
}
