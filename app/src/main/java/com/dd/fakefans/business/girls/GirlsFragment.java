package com.dd.fakefans.business.girls;

import com.dd.fakefans.Subscriber.SubscriberOnNextListener;
import com.dd.fakefans.base.BaseListFragment;
import com.dd.fakefans.entry.Tabs;
import com.dd.fakefans.entry.base.MeituInfo;

/**
 * Created by adong on 16/4/20.
 */
public class GirlsFragment extends BaseListFragment implements SubscriberOnNextListener<MeituInfo> {
    private Tabs tab;

    private GirlsPresenter girlsPresenter;
    private GirlsAdapter newsAdapter;
    public static final String KEY_TAB = "key_tab";
    private int page = 1;

    @Override
    public void onShow() {
        super.onShow();
        tab = (Tabs) getArguments().getSerializable(KEY_TAB);
        girlsPresenter = new GirlsPresenter();
        girlsPresenter.getGirls(getActivity(), this, tab.type, page + "");
        //  adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshData() {
        page = 1;
        girlsPresenter.getGirls(getActivity(), this, tab.type, page + "");
    }

    @Override
    public void onNext(MeituInfo data) {

        swipeRefreshLayout.setRefreshing(false);
        if (page == 1) {
            newsAdapter = new GirlsAdapter(getActivity(), data.getPagebean().getContentlist());
            newsAdapter.setOnlyOnce(false);
            recyclerView.setAdapter(newsAdapter);
        } else {
            newsAdapter.addAll(data.getPagebean().getContentlist());
        }
    }

    @Override
    public void onLoadMoreData() {
        page++;
        girlsPresenter.getGirls(getActivity(), this, tab.type, page + "");
    }

}
