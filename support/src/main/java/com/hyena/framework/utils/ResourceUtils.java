/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.utils;

import android.content.res.Resources;

/**
 * 资源获取通用类
 * @author yangzc on 15/8/25.
 */
public class ResourceUtils {

    /**
     * 通过名称获得布局ID
     * @param paramString
     * @return
     */
    public static int getLayoutId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "layout", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得字符串ID
     * @param paramString
     * @return
     */
    public static int getStringId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "string", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得DrawableID
     * @param paramString
     * @return
     */
    public static int getDrawableId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "drawable", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得样式ID
     * @param paramString
     * @return
     */
    public static int getStyleId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "style", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得ViewID
     * @param paramString
     * @return
     */
    public static int getId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "id", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得颜色ID
     * @param paramString
     * @return
     */
    public static int getColorId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "color", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得数组ID
     * @param paramString
     * @return
     */
    public static int getArrayId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "array", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 通过名称获得动画ID
     * @param paramString
     * @return
     */
    public static int getAnimId(String paramString) {
        return BaseApp.getAppContext().getResources()
                .getIdentifier(paramString, "anim", BaseApp.getAppContext().getPackageName());
    }

    /**
     * 获取系统内置参数
     */
    public static int getInternalDimensionSize(String paramString) {
        int result = 0;
        int resourceId = BaseApp.getAppContext().getResources().getIdentifier(paramString, "dimen", "android");
        if (resourceId > 0) {
            result = BaseApp.getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
