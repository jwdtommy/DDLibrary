/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统服务
 * @author yangzc on 15/9/8.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemService {

    String value() default "";

}
