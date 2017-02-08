/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app.fragment;

import com.hyena.framework.app.widget.EmptyView;
import com.hyena.framework.app.widget.LoadingView;
import com.hyena.framework.app.widget.TitleBar;

/**
 * 通用View构造器
 * @author yangzc on 15/8/20.
 */
public interface ViewBuilder {

    /**
     * 构筑通用标题栏
     * @param fragment
     * @return
     */
    public TitleBar buildTitleBar(BaseUIFragment<?> fragment);

    /**
     * 构筑通用EmptyView
     * @param fragment
     * @return
     */
    public EmptyView buildEmptyView(BaseUIFragment<?> fragment);

    /**
     * 构筑通用的LoadingView
     * @param fragment
     * @return
     */
    public LoadingView buildLoadingView(BaseUIFragment<?> fragment);
}
