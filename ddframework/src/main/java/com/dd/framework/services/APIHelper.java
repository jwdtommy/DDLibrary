package com.dd.framework.services;

/**
 * Created by J.Tommy on 17/2/16.
 */

public abstract class APIHelper {
	private CustomSubscriber mCustomSubscriber;

	public APIHelper(CustomSubscriber subscriber) {
		this.mCustomSubscriber = subscriber;
	}

	public CustomSubscriber getCustomSubscriber() {
		return mCustomSubscriber;
	}
}
