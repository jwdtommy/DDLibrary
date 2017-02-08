/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.span;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

/**
 * 可点击的ImageSpan
 * @author yangzc
 *
 */
public abstract class ClickableImageSpan extends VerticalImageSpan implements OnClickListener {

	public ClickableImageSpan(Context context, int resourceId,
			int verticalAlignment) {
		super(context, resourceId, verticalAlignment);
	}

	public ClickableImageSpan(Drawable d, int verticalAlignment) {
		super(d, verticalAlignment);
	}

	public ClickableImageSpan(Context context, int resourceId) {
		super(context, resourceId);
	}

	public ClickableImageSpan(Drawable d) {
		super(d);
	}
	
}
