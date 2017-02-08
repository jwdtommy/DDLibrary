/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hyena.framework.utils.UIUtils;

/**
 * 圆圈View </p>
 * 
 * @author yangzc
 *
 */
public class CircleHintView extends View {

	private Paint mBgPaint;
	private Paint mTxtPaint;
	private String mTipStr;

	private RectF mRectF = new RectF();

	public CircleHintView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CircleHintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleHintView(Context context) {
		super(context);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBgPaint.setColor(Color.RED);
		mBgPaint.setStyle(Style.FILL);

		mTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTxtPaint.setColor(Color.WHITE);
		mTxtPaint.setTextAlign(Align.CENTER);
		mTxtPaint.setTextSize(UIUtils.dip2px(12));
	}

	/**
	 * 设置颜色
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		mBgPaint.setColor(color);
		postInvalidate();
	}

	public void setTextSize(int dip) {
		mTxtPaint.setTextSize(UIUtils.dip2px(dip));
		postInvalidate();
	}

	public Paint getTxtPaint() {
		return mTxtPaint;
	}

	public Paint getBgPaint() {
		return mBgPaint;
	}

	/**
	 * 设置文本
	 * 
	 * @param tip
	 */
	public void setText(String tip) {
		this.mTipStr = tip;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mRectF.set(0, 0, getWidth(), getHeight());
		canvas.drawRoundRect(mRectF, mRectF.height()/2, mRectF.height()/2, mBgPaint);

		if (!TextUtils.isEmpty(mTipStr)) {
			int x = getWidth() / 2;
			
			FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
			float fontHeight = fontMetrics.bottom - fontMetrics.top;
			float textBaseY = getHeight() - (getHeight() - fontHeight) / 2
					- fontMetrics.bottom;
			canvas.drawText(mTipStr, x, textBaseY, mTxtPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float textWidth = 0;
		if (!TextUtils.isEmpty(mTipStr)) {
			textWidth = mTxtPaint.measureText(mTipStr) + getHeight()/2;
		}
		int width = (int) Math.max(textWidth, getSuggestedMinimumWidth());
		setMeasuredDimension(getSize(width, widthMeasureSpec),
				getSize(getSuggestedMinimumHeight(), heightMeasureSpec));
	}

	private int getSize(int size, int measureSpec) {
		int result = size;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
			case MeasureSpec.AT_MOST://wrap_content
				result = size;
				break;
			case MeasureSpec.EXACTLY://fill_parent or exactly
				result = specSize;
				break;
		}
		return result;
	}

}
