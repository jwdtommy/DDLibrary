/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.debug;

import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.hyena.framework.clientlog.LogUtil;

/**
 * @author yangzc on 15/9/7.
 */
public class DebugHelper {

	/**
	 * 注册View钩子
	 * @param view
	 */
    public static void hookView(View view){
        if(view == null)
            return;
        //递归注册钩子
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCnt = viewGroup.getChildCount();
            for (int i = 0; i < childCnt; i++) {
                View child = viewGroup.getChildAt(i);
                hookView(child);
            }
            hookClickListener(viewGroup);
        } else {
            hookClickListener(view);
        }
    }

    /**
     * 替换Click钩子
     * @param view
     */
    private static void hookClickListener(View view){
        if(view != null && view.isClickable()) {

            Object listenerInfo = InvokeHelper.getFieldValue(view, "mListenerInfo");
            if(listenerInfo != null) {
                final Object target = InvokeHelper.getFieldValue(listenerInfo, "mOnClickListener");

                if(target == null)
                    return;

                View.OnClickListener proxy = (View.OnClickListener) Proxy.newProxyInstance(view.getClass().getClassLoader(),
                        new Class[]{View.OnClickListener.class}, new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                LogUtil.v("DebugHelper", "===>>> onClick, proxy: " + proxy.getClass().getName());
                                return method.invoke(target, args);
                            }
                        });

                InvokeHelper.setFieldValue(listenerInfo, "mOnClickListener", proxy);
            }

        }
    }

}
