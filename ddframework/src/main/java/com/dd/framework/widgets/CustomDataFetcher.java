package com.dd.framework.widgets;

import com.dd.framework.services.CustomSubscriber;
import com.dd.framework.services.CustomSubscriberListener;
/**
 * Created by adong on 16/4/19.
 */
public abstract class CustomDataFetcher {
	private CustomSubscriber mCustomSubscriber;

	public CustomDataFetcher(CustomSubscriberListener<?> listener1,CustomSubscriberListener<?> listener2) {
		mCustomSubscriber = new CustomSubscriber(listener1,listener2);
	}

	public CustomSubscriber getCustomSubscriber() {
		return mCustomSubscriber;
	}
}
