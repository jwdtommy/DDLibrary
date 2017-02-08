package com.dd.fakefans.modules.News;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dd.fakefans.Subscriber.SubscriberOnNextListener;
import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.entry.Tabs;
import com.dd.fakefans.base.BaseListFragment;
import com.dd.fakefans.utils.Consts;

/**
 * Created by adong on 16/4/20.
 */
public class NewsFragment extends BaseListFragment implements SubscriberOnNextListener<BuDeJieInfo> {
    private Tabs tab;

    private NewsPresenter newsPresenter;
    private NewsAdapter newsAdapter;
    private int page = 1;

    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        tab = (Tabs) getArguments().getSerializable(Consts.KEY_TAB);
        newsPresenter = new NewsPresenter();
        newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }

    @Override
    public void onRefreshData() {
        page = 1;
        newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }

    @Override
    public void onNext(BuDeJieInfo data) {
        swipeRefreshLayout.setRefreshing(false);
        if (page == 1) {
            newsAdapter = new NewsAdapter(getActivity(), data.getPagebean().getContentlist());
            newsAdapter.setOnlyOnce(false);
            recyclerView.setAdapter(newsAdapter);
        } else {
            newsAdapter.addAll(data.getPagebean().getContentlist());
        }
    }

    @Override
    public void onLoadMoreData() {
        page++;
        newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }

}
