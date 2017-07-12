package com.dd.framework.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by adong on 16/4/20.
 */
public abstract class BaseListFragment<T extends FragmentHelper> extends CustomFragment {

    public SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreateImpl(@Nullable Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateCenterViewImpl(@Nullable Bundle savedInstanceState) {
        mSwipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        mRecyclerView = new RecyclerView(getActivity());
        mSwipeRefreshLayout.addView(mRecyclerView, new SwipeRefreshLayout.LayoutParams(-1, -1));
        return mSwipeRefreshLayout;
    }

    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getTopView().setVisibility(View.GONE);
        getBottomView().setVisibility(View.GONE);

        mSwipeRefreshLayout.setDistanceToTriggerSync(20);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                onLoadMoreData();
            }
        });
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
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
            lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
        }
        public abstract void onLoadMore();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
