/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.span;

import android.graphics.Rect;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Base of the custom Span
 * @author yangzc
 */
public abstract class BaseSpan extends ReplacementSpan implements OnClickListener {

	//the view has been attached
	private View mAttachView;
	
	public BaseSpan(View attachView) {
		this.mAttachView = attachView;
	}
	
	@Override
	public void onClick(View v) {}

	/**
	 * the span's rect
	 * @return
	 */
	public abstract Rect getRect();

	/**
	 * setFocus
	 * @param focus focus or not
	 */
	public abstract void setFocus(boolean focus);
	
	/**
	 * check is the position in span
	 * @return
	 */
	public abstract boolean isPositionIn(int x, int y);
	
	/**
	 * get view has been attached
	 * @return
	 */
	public View getAttachView(){
		return mAttachView;
	}
	
	/**
	 * post invalidate the attached view
	 */
	public void postInvalidate(){
		if (getAttachView() != null) {
			getAttachView().postInvalidate();
		}
	}
}
