/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * 垂直居中的ImageSpan
 * @author yangzc
 *
 */
public class VerticalImageSpan extends ImageSpan {
	
	//居中对齐
	public static final int ALIGN_CENTER = Integer.MAX_VALUE << 1;
	
	private int mVerticalAlignment = ALIGN_CENTER;
	
	public VerticalImageSpan(Context context, int resourceId,
			int verticalAlignment) {
		super(context, resourceId, verticalAlignment);
		this.mVerticalAlignment = verticalAlignment;
	}

	public VerticalImageSpan(Drawable d, int verticalAlignment) {
		super(d, verticalAlignment);
		this.mVerticalAlignment = verticalAlignment;
	}
	
	public VerticalImageSpan(Context context, int resourceId) {
		super(context, resourceId);
	}

	public VerticalImageSpan(Drawable d) {
		super(d);
	}

	public int getSize(Paint paint, CharSequence text, int start, int end,
			Paint.FontMetricsInt fontMetricsInt) {
		if (mVerticalAlignment != ALIGN_CENTER) {
			return super.getSize(paint, text, start, end, fontMetricsInt);
		}
		Drawable drawable = getDrawable();
		Rect rect = drawable.getBounds();
		if (fontMetricsInt != null) {
			Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
			int fontHeight = fmPaint.bottom - fmPaint.top;
			int drHeight = rect.bottom - rect.top;

			int top = drHeight / 2 - fontHeight / 4;
			int bottom = drHeight / 2 + fontHeight / 4;

			fontMetricsInt.ascent = -bottom;
			fontMetricsInt.top = -bottom;
			fontMetricsInt.bottom = top;
			fontMetricsInt.descent = top;
		}
		return rect.right;
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		if (mVerticalAlignment != ALIGN_CENTER) {
			super.draw(canvas, text, start, end, x, top, y, bottom, paint);
			return;
		}
		Drawable drawable = getDrawable();
		canvas.save();
		int transY = 0;
		transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
		canvas.translate(x, transY);
		drawable.draw(canvas);
		canvas.restore();
	}

}
