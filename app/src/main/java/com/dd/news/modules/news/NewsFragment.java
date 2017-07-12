package com.dd.news.modules.news;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.dd.news.entry.Tabs;
import com.dd.news.utils.Consts;
import com.dd.framework.base.BaseListFragment;
/**
 * Created by adong on 16/4/20.
 */
public class NewsFragment extends BaseListFragment {
    private Tabs tab;

    private NewsPresenter newsPresenter;
    private NewsAdapter newsAdapter;
    private int page = 1;

    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        tab = (Tabs) getArguments().getSerializable(Consts.KEY_TAB);
//        newsPresenter = new NewsPresenter();
    }

    @Override
    public void onRefreshData() {
        page = 1;
   //     newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }

//    @Override
//    public void onNext(BuDeJieInfo data) {
//        mSwipeRefreshLayout.setRefreshing(false);
//        if (page == 1) {
//            newsAdapter = new NewsAdapter(getActivity(), data.getPagebean().getContentlist());
//            newsAdapter.setOnlyOnce(false);
//            mRecyclerView.setAdapter(newsAdapter);
//        } else {
//            newsAdapter.addAll(data.getPagebean().getContentlist());
//        }
//    }

    @Override
    public void onLoadMoreData() {
        page++;
   //     newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("onKeyDown","News");
        return super.onKeyDown(keyCode,event);
    }
}
