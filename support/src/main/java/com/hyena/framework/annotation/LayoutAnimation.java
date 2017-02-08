/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.annotation;


/**
 * 布局动画
 * @author yangzc
 */
public @interface LayoutAnimation {

	/**
	 * 执行时间
	 * @return
	 */
    int duration() default 200;
    
    
    /**
     * 开始的X位置
     * @return
     */
    int offsetX();
    
    /**
     * 开始的Y位置
     * @return
     */
    int offsetY();
}
