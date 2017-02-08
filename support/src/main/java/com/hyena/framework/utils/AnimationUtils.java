/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 动画通用类
 * 
 * @author yangzc
 */
public class AnimationUtils {

	/**
	 * 展开
	 * 
	 * @param view
	 * @param listener
	 */
	public static void expand(final View view, AnimatorListener listener) {
		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setDuration(200);
		animator.setInterpolator(new AccelerateInterpolator());
		final int height = view.getMeasuredHeight();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				view.getLayoutParams().height = (int) (height * (Float) animation
						.getAnimatedValue());
			}
		});
		animator.addListener(listener);
		animator.start();
	}

	/**
	 * 合并
	 * 
	 * @param view
	 * @param listener
	 */
	public static void unExpand(final View view, AnimatorListener listener) {
		ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
		animator.setDuration(200);
		animator.setInterpolator(new AccelerateInterpolator());
		final int height = view.getMeasuredHeight();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				view.getLayoutParams().height = (int) (height * (Float) animation
						.getAnimatedValue());
			}
		});
		animator.addListener(listener);
		animator.start();
	}

	/**
	 * ValueAnimator监听器
	 * @author yangzc
	 */
	public static interface ValueAnimatorListener extends
			AnimatorUpdateListener, AnimatorListener {
	}
}
