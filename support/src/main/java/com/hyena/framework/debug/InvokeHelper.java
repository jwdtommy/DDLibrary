/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.debug;

import java.lang.reflect.Field;

/**
 * 反射相关控制类
 * @author yangzc on 15/9/7.
 */
public class InvokeHelper {

    /**
     * 获得字段值
     * @param object
     * @param name
     * @return
     */
    public static Object getFieldValue(Object object, String name) {
        try {
            Field field = getDeclaredField(object.getClass(), name);
            if(field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置字段值
     * @param object
     * @param name
     * @param value
     */
    public static void setFieldValue(Object object, String name, Object value){
        try {
            Field field = getDeclaredField(object.getClass(), name);
            if(field != null) {
                field.setAccessible(true);
                field.set(object, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得参数Field
     * @param cls
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Class<?> cls, String fieldName){
        try {
            Field field = cls.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            if(cls.getSuperclass() != null) {
                return getDeclaredField(cls.getSuperclass(), fieldName);
            }
        }
        return null;
    }
}
