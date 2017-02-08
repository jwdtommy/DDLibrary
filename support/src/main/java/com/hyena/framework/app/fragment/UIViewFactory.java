/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app.fragment;

import com.hyena.framework.app.widget.DefaultUIViewBuilder;
import com.hyena.framework.app.widget.EmptyView;
import com.hyena.framework.app.widget.LoadingView;
import com.hyena.framework.app.widget.TitleBar;

/**
 * View构造器
 * @author yangzc on 15/8/20.
 */
public class UIViewFactory {

    public static UIViewFactory instance;
    private ViewBuilder mViewBuilder = new DefaultUIViewBuilder();

    private UIViewFactory(){}

    public static UIViewFactory getViewFactory(){
        if(instance == null)
            instance = new UIViewFactory();
        return instance;
    }

    /**
     * 注册View构造器
     * @param builder
     */
    public void registViewBuilder(ViewBuilder builder){
        this.mViewBuilder = builder;
    }

    /**
     * 构筑通用标题栏
     * @param fragment
     * @return
     */
    public TitleBar buildTitleBar(BaseUIFragment<?> fragment){
        return mViewBuilder.buildTitleBar(fragment);
    }

    /**
     * 构筑通用的EmptyView
     * @param fragment
     * @return
     */
    public EmptyView buildEmptyView(BaseUIFragment<?> fragment){
        return mViewBuilder.buildEmptyView(fragment);
    }

    /**
     * 构筑通用的LoadingView
     * @param fragment
     * @return
     */
    public LoadingView buildLoadingView(BaseUIFragment<?> fragment){
        return mViewBuilder.buildLoadingView(fragment);
    }
}
