/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.animation.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * @Author Yangzc
 * @Date 2013年10月29日
 * @Version 1.0
 *
 */
public class AnimUtils {

	public static void repeat(ImageView imageView){
		TranslateAnimation alphaAnimation2 = new TranslateAnimation(150f, 350f, 50, 50);  
		alphaAnimation2.setDuration(1000);  
		alphaAnimation2.setRepeatCount(Animation.INFINITE);  
		alphaAnimation2.setRepeatMode(Animation.REVERSE);  
		imageView.setAnimation(alphaAnimation2);  
		alphaAnimation2.start();  
	}
	
}

