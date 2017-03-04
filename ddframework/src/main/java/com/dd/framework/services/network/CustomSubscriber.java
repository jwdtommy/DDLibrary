package com.dd.framework.services.network;

import android.content.Context;

import com.dd.framework.base.BaseResult;

import rx.Subscriber;
/**
 */
public class CustomSubscriber extends Subscriber<BaseResult> {
	private Context context;
	private CustomSubscriberListener mCustomSubscriberListener1;
	private CustomSubscriberListener mCustomSubscriberListener2;

	public CustomSubscriber(CustomSubscriberListener customSubscriberListener1,CustomSubscriberListener customSubscriberListener2) {
		this.mCustomSubscriberListener1 = customSubscriberListener1;
		this.mCustomSubscriberListener2 = customSubscriberListener2;
	}

	/**
	 * 订阅开始时调用
	 * 显示ProgressDialog
	 */
	@Override
	public void onStart() {
		mCustomSubscriberListener1.onLoading();
		mCustomSubscriberListener2.onLoading();
	}

	/**
	 * 完成，隐藏ProgressDialog
	 */
	@Override
	public void onCompleted() {
		mCustomSubscriberListener1.onCompleted();
		mCustomSubscriberListener2.onCompleted();
	}

	@Override
	public void onError(Throwable e) {
//		if (e instanceof ApiException) {
//			mCustomSubscriberListener1.onError((ApiException) e);
//			mCustomSubscriberListener2.onError((ApiException) e);
//		} else {
			mCustomSubscriberListener1.onError(e);
			mCustomSubscriberListener2.onError(e);
//		}
	}

	/**
	 * 将onNext方法中的返回结果交给Activity或Fragment自己处理
	 * @param t 创建Subscriber时的泛型类型
	 */
	@Override
	public void onNext(BaseResult t) {
		mCustomSubscriberListener1.onNext(t);
		mCustomSubscriberListener2.onNext(t);
	}
}