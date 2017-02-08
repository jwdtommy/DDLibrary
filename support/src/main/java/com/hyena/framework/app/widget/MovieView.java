/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MovieView extends View {

	private ValueAnimator animator;
	private Bitmap mMovieBitmap = null;
	private int mOffset = 0;
	private Paint mBGPaint;
	private Paint mShaderPaint;
	private Path mPath;

	public MovieView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public MovieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MovieView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBGPaint.setColor(0xfffbf39a);
		
		mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mShaderPaint.setColor(0xffffd33a);
		mShaderPaint.setAlpha((int) (255 * 0.4));
		
		mPath = new Path();
	}

	public void setMoveBitmap(Bitmap bitmap) {
		this.mMovieBitmap = bitmap;
		start();
	}

	public void start() {
		if (mMovieBitmap == null || mMovieBitmap.isRecycled()) {
			return;
		}
		if (animator != null) {
			animator.cancel();
		}
		animator = ValueAnimator.ofFloat(0.0f, 1.0f);
		animator.setDuration(5000);
		animator.setInterpolator(new LinearInterpolator());
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mOffset = (int) (mMovieBitmap.getWidth() * (Float) animation
						.getAnimatedValue());
				postInvalidate();
			}
		});
		animator.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mMovieBitmap != null) {
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2,
					mBGPaint);
			
			float scale = getHeight() / mMovieBitmap.getHeight();
			canvas.save();
			
			mPath.reset();
			mPath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Direction.CCW);
			canvas.clipPath(mPath);
			
			canvas.translate(0, getHeight() *2.0f/ 3);
			canvas.rotate(-30);
			canvas.scale(scale, scale);
			
			int x = -mOffset;
			do {
				canvas.drawBitmap(mMovieBitmap, x, 0, null);	
				x += mMovieBitmap.getWidth();
			} while(x < getWidth());

			canvas.restore();
			
			canvas.save();

			mPath.reset();
			mPath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Direction.CCW);
			canvas.clipPath(mPath);
			
			mPath.reset();
			mPath.moveTo(0, getHeight()*3.0f/4);
			mPath.quadTo(getWidth(), getHeight()* 3.0f /4, getWidth(), 0);
			
			mPath.quadTo(getWidth()*2, getHeight()*3, 0, getHeight()*3.0f/4);
			canvas.drawPath(mPath, mShaderPaint);
			canvas.restore();
		}
	}
}
