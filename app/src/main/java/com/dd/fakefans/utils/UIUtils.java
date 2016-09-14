package com.dd.fakefans.utils;

import android.app.Application;

/**
 * Created by J.Tommy on 16/9/14.
 */
public class UIUtils {
    private static Application context;

    public static void init(Application app) {
        if (context == null)
            context = app;
    }

    public static int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
