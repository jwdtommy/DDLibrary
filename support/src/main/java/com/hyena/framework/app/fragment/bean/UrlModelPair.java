/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.fragment.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;

public class UrlModelPair {

	public String mUrl;
	
	public BaseObject mOnlineObject;

	public UrlModelPair(String url, BaseObject object){
		this.mUrl = url;
		this.mOnlineObject = object;
	}
	
	public boolean isEmpty(){
		return TextUtils.isEmpty(mUrl) || mOnlineObject == null;
	}
}
