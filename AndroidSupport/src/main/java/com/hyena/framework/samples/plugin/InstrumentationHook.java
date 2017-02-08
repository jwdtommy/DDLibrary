package com.hyena.framework.samples.plugin;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yangzc on 16/6/29.
 */
public class InstrumentationHook {

    public static void hook(){
        try {
            Class<?> activityThreadClz = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClz
                    .getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            Field instrumentationField = activityThreadClz.getDeclaredField("mInstrumentation");
            instrumentationField.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) instrumentationField.get(currentActivityThread);
            if (!(instrumentation instanceof PluginInstrumentation)) {
                PluginInstrumentation pluginInstrumentation = new PluginInstrumentation(instrumentation);
                instrumentationField.set(currentActivityThread, pluginInstrumentation);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static class PluginInstrumentation extends Instrumentation {

        private Instrumentation mBaseInstrumentation;
        public PluginInstrumentation(Instrumentation instrumentation){
            this.mBaseInstrumentation = instrumentation;
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent)
                throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            //replace plugin className
            String clzName = className;
            if (intent != null) {
                String pluginClassName = intent.getStringExtra(ARGS_BUNDLE_PLUGIN_CLZ);
                if (!TextUtils.isEmpty(pluginClassName)) {
                    clzName = pluginClassName;
                }
            }
            return super.newActivity(cl, clzName, intent);
        }

        @Override
        public Application newApplication(ClassLoader cl, String className, Context context)
                throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            return super.newApplication(cl, className, context);
        }

        /**
         * use for plugin startActivity
         * Override execStartActivity method
         */
        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {
            replaceIntentTargetIfNeed(who, intent);
            try {
                Method execStartActivityMethod = Instrumentation.class
                        .getDeclaredMethod("execStartActivity", Context.class, IBinder.class,
                                IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
                execStartActivityMethod.setAccessible(true);
                return (ActivityResult) execStartActivityMethod.invoke(mBaseInstrumentation,
                        who, contextThread, token, target, intent, requestCode, options);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void replaceIntentTargetIfNeed(Context who, Intent intent) {
            if (intent != null) {
                //append plugin class name
                if (intent.getComponent() != null && intent.getComponent().getClassName() != null) {
                    intent.putExtra(ARGS_BUNDLE_PLUGIN_CLZ, intent.getComponent().getClassName());
                }
                intent.setClass(who, StubActivity.StubStandActivity.class);
            }
        }
    }

    public static final String ARGS_BUNDLE_PLUGIN_CLZ = "plugin_clz";
}
