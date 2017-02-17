package com.dd.framework.utils;

import android.app.Application;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by J.Tommy on 16/9/14.
 */
public class UIUtils {
    private static Application context;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

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

    public static void setViewId(View view){
        if(view==null) {
            throw new RuntimeException("setViewId but the view is null!");
        }
       view.setId(generateViewId());
    }
}
