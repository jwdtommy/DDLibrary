package com.hyena.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by yangzc on 16/9/16.
 */

public abstract class AbsRefreshablePanel extends RelativeLayout {

    public static final int STATUS_RESET = 1;//状态重置
    public static final int STATUS_START_PULL = 2;//拖动中
    public static final int STATUS_READY_REFRESH = 3;//拖动超过阀值
    public static final int STATUS_REFRESH = 4;//加载中

    public AbsRefreshablePanel(Context context) {
        super(context);
    }

    public AbsRefreshablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsRefreshablePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setStatus(int status);

    public void setScrolling(float scrollY, float maxScrollY) {}

    public abstract int getContentHeight();
}
