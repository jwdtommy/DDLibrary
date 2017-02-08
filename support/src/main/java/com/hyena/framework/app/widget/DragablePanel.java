/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * 退拽ButtonPanel
 * @author yangzc
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class DragablePanel extends RelativeLayout {

	private boolean mDraging = false;
	private static final int PADDING = 10;

	public DragablePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}
	
	protected View getDragView(){
		return null;
	}

	private float mDownX, mDownY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mMoving = false;
			mDownX = ev.getX();
			mDownY = ev.getY();
			int padding = UIUtils.dip2px(PADDING);
			Rect outRect = new Rect(getDragView().getLeft() - padding,
					getDragView().getTop() - padding, getDragView().getLeft()
							+ getDragView().getWidth() + padding,
							getDragView().getTop() + getDragView().getHeight() + padding);
			mDraging = outRect.contains((int) mDownX, (int) mDownY);
			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			mDraging = false;
			mMoving = false;
			break;
		}
		default:
			break;
		}
		return mDraging;
	}

	private float mLastX, mLastY;
	private boolean mMoving = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mMoving = false;
			mLastX = x;
			mLastY = y;
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			float disX = x - mLastX;
			float disY = y - mLastY;
			getParent().requestDisallowInterceptTouchEvent(true);
			if (Math.abs(disX) > UIUtils.dip2px(2)
					&& Math.abs(disY) > UIUtils.dip2px(2)) {
				mMoving = true;
				LayoutParams params = (LayoutParams) getDragView()
						.getLayoutParams();
				params.rightMargin = (int) (params.rightMargin - disX);
				params.bottomMargin = (int) (params.bottomMargin - disY);

				if (params.rightMargin > getWidth() - getDragView().getWidth()) {
					params.rightMargin = getWidth() - getDragView().getWidth();
				}
				if (params.rightMargin < 0) {
					params.rightMargin = 0;
				}
				if (params.bottomMargin > getHeight() - getDragView().getHeight()) {
					params.bottomMargin = getHeight() - getDragView().getHeight();
				}
				if (params.bottomMargin < 0) {
					params.bottomMargin = 0;
				}
				requestLayout();
				mLastX = x;
				mLastY = y;
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			getParent().requestDisallowInterceptTouchEvent(false);
			if (!mMoving) {
				onClick();
			} else {
				stickBorder();
			}
			mDraging = false;
			mMoving = false;
			break;
		}
		default:
			break;
		}
		return mDraging;
	}

	private void onClick() {
		getDragView().performClick();
//		mAnimView.setVisibility(View.VISIBLE);
//		Animator scalex = ObjectAnimator.ofFloat(mDragView, "scaleX", 1.5f);
//		Animator scaley = ObjectAnimator.ofFloat(mDragView, "scaleY", 1.5f);
//		Animator alpha = ObjectAnimator.ofFloat(mDragView, "alpha", 1.0f, 0.0f);
//		
//		AnimatorSet scale = new AnimatorSet();
//		scale.setDuration(100);
//		scale.play(scalex).with(scaley).with(alpha);
//		scale.addListener(new AnimatorListener() {
//			@Override
//			public void onAnimationStart(Animator animation) {
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//			}
//			
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				ViewHelper.setScaleX(mDragView, 1);
//				ViewHelper.setScaleY(mDragView, 1);
//				ViewHelper.setAlpha(mDragView, 1);
//				mAnimView.setVisibility(View.GONE);
//				mDragView.performClick();
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator animation) {
//			}
//		});
//		scale.start();
	}

	private boolean toLeft = true;

	/**
	 * 自动粘贴到边框
	 */
	private void stickBorder() {
		int left = getDragView().getLeft();
		if (left < (getWidth() - getDragView().getWidth()) / 2) {
			toLeft = true;
		} else {
			toLeft = false;
		}
		ViewPropertyAnimator
				.animate(getDragView())
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.setDuration(200)
				.translationX(
						toLeft ? -getDragView().getLeft() + UIUtils.dip2px(6)
								: getWidth() - getDragView().getLeft()
										- getDragView().getWidth()
										- UIUtils.dip2px(6))
				.setListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						LayoutParams params = (LayoutParams) getDragView()
								.getLayoutParams();
						ViewHelper.setTranslationX(getDragView(), 0);
						if (toLeft) {
							params.rightMargin = getWidth()
									- getDragView().getWidth() - UIUtils.dip2px(6);
						} else {
							params.rightMargin = UIUtils.dip2px(6);
						}
						requestLayout();
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				}).start();
	}
}
