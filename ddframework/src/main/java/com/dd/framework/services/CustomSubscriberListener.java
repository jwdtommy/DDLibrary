package com.dd.framework.services;

import com.dd.framework.base.BaseResult;

/**
 * Created by J.Tommy on 17/2/16.
 */

public interface CustomSubscriberListener<T> {
	void onLoading();

	void onNext(T baseResult);

	void onCompleted();

	void onError(ApiException apiException);

	public static class CustomSubscriberAdapter<T> implements CustomSubscriberListener<T> {


		@Override
		public void onLoading() {

		}

		@Override
		public void onNext(T baseResult) {

		}

		@Override
		public void onCompleted() {

		}

		@Override
		public void onError(ApiException apiException) {

		}
	}
}
