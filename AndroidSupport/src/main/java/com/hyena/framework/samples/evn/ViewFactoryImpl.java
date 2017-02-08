package com.hyena.framework.samples.evn;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.ViewBuilder;
import com.hyena.framework.app.widget.CommonEmptyView;
import com.hyena.framework.app.widget.CommonTitleBar;
import com.hyena.framework.app.widget.EmptyView;
import com.hyena.framework.app.widget.LoadingView;
import com.hyena.framework.app.widget.TitleBar;

/**
 * Created by yangzc on 16/8/5.
 */
public class ViewFactoryImpl implements ViewBuilder {

    @Override
    public TitleBar buildTitleBar(BaseUIFragment<?> fragment) {
        CommonTitleBar bar = new CommonTitleBar(fragment.getActivity());
        bar.setBaseUIFragment(fragment);
        return bar;
    }

    @Override
    public EmptyView buildEmptyView(BaseUIFragment<?> fragment) {
        return new CommonEmptyView(fragment.getActivity());
    }

    @Override
    public LoadingView buildLoadingView(BaseUIFragment<?> fragment) {
        return new LoadingView(fragment.getActivity()) {
            @Override
            public void showLoading(String hint) {

            }
        };
    }
}
