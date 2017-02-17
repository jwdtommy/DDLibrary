package com.dd.news.modules.girls;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dd.news.Subscriber.SubscriberOnNextListener;
import com.dd.news.entry.Tabs;
import com.dd.news.entry.MeituInfo;
import com.dd.framework.base.BaseListFragment;

/**
 * Created by adong on 16/4/20.
 */
public class GirlsFragment extends BaseListFragment {
    private Tabs tab;

    private GirlsPresenter girlsPresenter;
    private GirlsAdapter newsAdapter;
    public static final String KEY_TAB = "key_tab";
    private int page = 1;

    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        tab = (Tabs) getArguments().getSerializable(KEY_TAB);
        girlsPresenter = new GirlsPresenter();
  //      girlsPresenter.getGirls(getActivity(), this, tab.type, page + "");
        //  adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshData() {
        page = 1;
  //      girlsPresenter.getGirls(getActivity(), this, tab.type, page + "");
    }
//
//    @Override
//    public void onNext(MeituInfo data) {
//        mSwipeRefreshLayout.setRefreshing(false);
//        if (page == 1) {
//            newsAdapter = new GirlsAdapter(getActivity(), data.getPagebean().getContentlist());
//            newsAdapter.setOnlyOnce(false);
//            mRecyclerView.setAdapter(newsAdapter);
//        } else {
//            newsAdapter.addAll(data.getPagebean().getContentlist());
//        }
//    }

    @Override
    public void onLoadMoreData() {
        page++;
     //   girlsPresenter.getGirls(getActivity(), this, tab.type, page + "");
    }

}
