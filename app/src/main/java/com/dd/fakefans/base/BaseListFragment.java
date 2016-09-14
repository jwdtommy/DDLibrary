package com.dd.fakefans.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.fakefans.R;

import butterknife.Bind;

/**
 * Created by adong on 16/4/20.
 */
public abstract class BaseListFragment extends BaseFragment {

    @Bind(R.id.layout_refresh)
    public SwipeRefreshLayout swipeRefreshLayout;
    @Bind((R.id.rv_content))
    public RecyclerView recyclerView;

    public LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onShow() {
        swipeRefreshLayout.setDistanceToTriggerSync(20);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                onLoadMoreData();
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public int configView() {
        return R.layout.fragment_home_list;
    }

    public abstract void onRefreshData();

    public abstract void onLoadMoreData();


    private abstract class EndlessRecyclerOnScrollListener extends
            RecyclerView.OnScrollListener {
        int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;


        private LinearLayoutManager mLinearLayoutManager;

        public EndlessRecyclerOnScrollListener(
                LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mLinearLayoutManager.getItemCount()) {
                onLoadMore();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        }

        public abstract void onLoadMore();
    }
}
