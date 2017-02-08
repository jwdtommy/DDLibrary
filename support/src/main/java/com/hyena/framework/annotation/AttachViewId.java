/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关联View注解
 * @author yangzc on 15/9/8.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttachViewId {

    int value() default 0;
}
