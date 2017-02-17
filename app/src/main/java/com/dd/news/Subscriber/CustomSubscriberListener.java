package com.dd.news.Subscriber;

import com.dd.framework.base.BaseResult;
import com.dd.framework.services.ApiException;
/**
 * Created by J.Tommy on 17/2/16.
 */

public interface CustomSubscriberListener {
	void onStart();
	void onloading();
	void onNext(BaseResult baseResult);
	void onCompleted();
	void onError(ApiException apiException);
}
