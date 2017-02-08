/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 数字版
 * @author yangzc
 */
public class NumberBoard extends View {

	private int mNumber;
	private Paint mPaint;

	private float mRange = 0;

	public NumberBoard(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public NumberBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NumberBoard(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(UIUtils.dip2px(20));
	}
	
	/**
	 * 文字大小
	 * @param textSize
	 */
	public void setTextSize(int textSize){
		mPaint.setTextSize(UIUtils.dip2px(textSize));
		postInvalidate();
	}
	
	/**
	 * 文字颜色
	 * @param color
	 */
	public void setTextColor(int color){
		mPaint.setColor(color);
		postInvalidate();
	}

	/**
	 * 设置数字
	 * @param num
	 */
	public void setNumber(int num) {
		this.mNumber = num;
		play();
	}

	/**
	 * 播放动画
	 */
	private void play() {
		ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
		animator.setDuration(1000);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				mRange = animator.getAnimatedFraction();
				postInvalidate();
			}
		});
		animator.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		char[] number = String.valueOf(mNumber).toCharArray();
		float x = (getWidth() - mPaint.measureText(mNumber + "")) / 2;
		float lineHeight = mPaint.measureText("H");
		int paddingV = UIUtils.dip2px(2);
		//控制垂直居中
		float offsetY = (getHeight() - (lineHeight + paddingV * 2))/2;
//		if (FrameworkConfig.getConfig().isDebug()) {
//			canvas.drawLine(0, offsetY, getWidth(), offsetY, mPaint);
//			canvas.drawLine(0, lineHeight + offsetY + paddingV * 2, getWidth(), lineHeight + offsetY + paddingV * 2, mPaint);
//		}
		canvas.save();
		canvas.clipRect(0, offsetY, getWidth(), lineHeight + offsetY + paddingV * 2);
		
		for (int i = 0; i < number.length; i++) {
			int numberItem = (number[i] - '0');
			float y = -(lineHeight + paddingV * 2) * (numberItem) * mRange + offsetY;
			for (int j = 0; j <= numberItem; j++) {
				canvas.drawText(j + "", x, y - paddingV + lineHeight + paddingV * 2 , mPaint);
				y += (lineHeight + paddingV * 2);
			}
			x += lineHeight;
		}
		canvas.restore();
	}
}
