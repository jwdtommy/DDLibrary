/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;

import com.hyena.framework.app.coretext.span.FillInSpan.OnRectChangeListener;
import com.hyena.framework.utils.UIUtils;

/**
 * 分数公式
 * @author yangzc
 */
public class FractionSpan extends FillInSpan implements OnRectChangeListener {
	
	//分子输入框
	private SingleFillInSpan mNumeratorFillInSpan;
	//分母输入框
	private SingleFillInSpan mDenominatorFillinSpan;
	private Rect mNumberatorFillInRect = new Rect();
	private Rect mDenominatorFillInRect = new Rect();
	
	private FillInSpan mSelectFillIn;
	private Paint mLinePaint;
	
	public FractionSpan(View attachView, String numerator, String denominator) {
		super(attachView);
		//初始化分子
		mNumeratorFillInSpan = new SingleFillInSpan(attachView, "123");
		mNumeratorFillInSpan.setRectChangeListener(this);
		//初始化分母
		mDenominatorFillinSpan = new SingleFillInSpan(attachView, "456");
		mDenominatorFillinSpan.setRectChangeListener(this);

		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(UIUtils.dip2px(2));
	}
	
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end,
			FontMetricsInt fontMetricsInt) {
		int border[] = mDenominatorFillinSpan.getBorder(paint);
		if (fontMetricsInt != null) {
			fontMetricsInt.ascent = -border[1] * 2;
			fontMetricsInt.top = -border[1] * 2;
			fontMetricsInt.bottom = border[0] * 2;
			fontMetricsInt.descent = border[0] * 2;
		}
		return (int) getWidth();
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		float width = getWidth();
		mNumberatorFillInRect.set((int) x, top, (int) (x + width), top + FILLIN_HEIGHT);
		mDenominatorFillInRect.set((int) x, top + FILLIN_HEIGHT, (int) (x + width), bottom);
		
		mNumeratorFillInSpan.drawImpl(canvas, mNumberatorFillInRect);
		mDenominatorFillinSpan.drawImpl(canvas, mDenominatorFillInRect);
		
		int stopY = (bottom + top)/2;
		canvas.drawLine(x, stopY, x + width, stopY, mLinePaint);
	}
	
	@Override
	public float getWidth() {
		float numberatorW = mNumeratorFillInSpan.getWidth();
		float denominatorW = mDenominatorFillinSpan.getWidth();
		
		float width = numberatorW;
		if (width < denominatorW) {
			width = denominatorW;
		}
		return width;
	}
	
	@Override
	public boolean isPositionIn(int x, int y) {
		if (mNumberatorFillInRect.contains(x, y)) {
			mSelectFillIn = mNumeratorFillInSpan;
			return true;
		} else if(mDenominatorFillInRect.contains(x, y)){
			mSelectFillIn = mDenominatorFillinSpan;
			return true;
		} else {
			mSelectFillIn = null;
			return false;
		}
	}
	
	@Override
	public void setText(String text) {
		if (mSelectFillIn != null) {
			mSelectFillIn.setText(text);
		}
	}
	
	@Override
	public String getText() {
		if (mSelectFillIn != null) {
			return mSelectFillIn.getText();
		}
		return "";
	}

	@Override
	public void setFocus(boolean focus) {
		mNumeratorFillInSpan.setFocus(false);
		mDenominatorFillinSpan.setFocus(false);
		if (focus && mSelectFillIn == null) {
			mSelectFillIn = mDenominatorFillinSpan;
		}
		if (mSelectFillIn != null) {
			mSelectFillIn.setFocus(focus);
		}
	}
	

	@Override
	public float getContentWidth() {
		return 0;
	}

	@Override
	public void onRectChange(Rect rect) {
		FILLIN_WIDTH = rect.width();
		postInvalidate();
	}
	
}
