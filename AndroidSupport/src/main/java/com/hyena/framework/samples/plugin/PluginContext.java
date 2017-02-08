package com.hyena.framework.samples.plugin;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by yangzc on 16/6/29.
 */
public class PluginContext extends ContextWrapper {

    public PluginContext(Context base) {
        super(base);
    }
}
