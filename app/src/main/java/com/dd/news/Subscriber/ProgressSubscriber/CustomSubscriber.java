package com.dd.news.Subscriber.ProgressSubscriber;

import android.content.Context;

import com.dd.framework.base.BaseResult;
import com.dd.framework.services.ApiException;
import com.dd.news.Subscriber.CustomSubscriberListener;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 */
public class CustomSubscriber extends Subscriber<BaseResult> {
	private Context context;
	private CustomSubscriberListener mCustomSubscriberListener;

	public CustomSubscriber(Context context, CustomSubscriberListener customSubscriberListener) {
		this.context = context;
		this.mCustomSubscriberListener = customSubscriberListener;
	}

	/**
	 * 订阅开始时调用
	 * 显示ProgressDialog
	 */
	@Override
	public void onStart() {
		mCustomSubscriberListener.onStart();
	}

	/**
	 * 完成，隐藏ProgressDialog
	 */
	@Override
	public void onCompleted() {
		mCustomSubscriberListener.onCompleted();
	}

	@Override
	public void onError(Throwable e) {
		if (e instanceof ApiException) {
			mCustomSubscriberListener.onError((ApiException) e);
		} else {
			e.printStackTrace();
		}
	}

	/**
	 * 将onNext方法中的返回结果交给Activity或Fragment自己处理
	 * @param t 创建Subscriber时的泛型类型
	 */
	@Override
	public void onNext(BaseResult t) {
		mCustomSubscriberListener.onNext(t);
	}
}