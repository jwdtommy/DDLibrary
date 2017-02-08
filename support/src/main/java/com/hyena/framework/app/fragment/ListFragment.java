package com.hyena.framework.app.fragment;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.app.widget.ListLoadingMoreFooter;
import com.hyena.framework.app.widget.LoadMoreListView;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.network.NetworkProvider;

/**
 * 具有listView功能的Fragment
 * Created by yangzc on 16/2/15.
 */
public abstract class ListFragment<T extends BaseUIFragmentHelper, K> extends BaseUIFragment<T> {

    private static final String TAG = "ListFragment";

    protected SwipeRefreshLayout mSrlPanel;
    protected LoadMoreListView mLvListView;
    protected ListLoadingMoreFooter mLvFooter;
    protected SingleTypeAdapter<K> mListAdapter;

    private boolean mHasMore = true;
    private boolean mEnableLoadMore = true;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        mListAdapter = getListAdapter();
        if (!isValid())
            return null;
        mSrlPanel = newSwipeRefreshLayout();

        mLvListView = newLoadMoreListView();
        if (mEnableLoadMore)
            mLvListView.initFooter(mLvFooter = newFooterView());

        mLvListView.setAdapter(mListAdapter);
        mLvListView.setEnableLoadMore(mEnableLoadMore);
        setLoadMoreText("正在加载中...");

        if (mLvListView.getParent() == null)
            mSrlPanel.addView(mLvListView);

        mSrlPanel.setOnRefreshListener(mRefreshListener);

        if (mEnableLoadMore)
            mLvListView.setOnLastItemVisibleListener(mLastItemVisibleListener);
        return mSrlPanel;
    }

    public void disableLoadMore() {
        this.mEnableLoadMore = false;
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener
            = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //load first page
            mLvListView.setLoadStatus(false);
//            loadDefaultData(PAGE_FIRST);
            refresh();
        }
    };

    private LoadMoreListView.OnLastItemVisibleListener mLastItemVisibleListener
            = new LoadMoreListView.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            if (!NetworkProvider.getNetworkProvider()
                    .getNetworkSensor().isNetworkAvailable()) {
                return;
            }
            if (hasMore()) {
                mLvListView.setLoadStatus(true);
                //load more from net
//                loadDefaultData(PAGE_MORE);
                loadMore();
            } else {
                LogUtil.v(TAG, "At the end of the listView");
            }
        }
    };

    public void refresh() {
        setHasMore(true);
        loadDefaultData(PAGE_FIRST);
    }

    public void loadMore() {
        loadDefaultData(PAGE_MORE);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
        if (!isValid())
            return;
        if (pageNo == PAGE_FIRST) {
            if (mListAdapter.isEmpty()) {
                //first load
                mSrlPanel.setRefreshing(false);
            } else {
                //refresh
                mSrlPanel.setRefreshing(true);
                showContent();
            }
        } else {
            //load more
            mSrlPanel.setRefreshing(false);
            showContent();
        }
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        return super.getRequestUrlModelPair(action, pageNo, params);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (!isValid())
            return;
        //close refresh status
        mSrlPanel.setRefreshing(false);
        mLvListView.setLoadStatus(false);

        List<K> dataList = getList(result);

        if (pageNo == PAGE_FIRST) {
            if (dataList != null && !dataList.isEmpty()) {
                mListAdapter.setItems(dataList);
            } else {
                setHasMore(false);
                showEmpty();
            }
        } else {
            if (dataList != null && !dataList.isEmpty()) {
                mListAdapter.addItems(dataList);
            } else {
                setHasMore(false);
            }
        }
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        if (!isValid())
            return;
        if (pageNo == PAGE_FIRST) {
            List<K> dataList = getList(result);
            if (dataList != null && !dataList.isEmpty()) {
                mListAdapter.setItems(dataList);
                super.onGetCache(action, pageNo, result);
            }
        }
    }

    protected void showEmpty() {
        getEmptyView().showEmpty("", "暂无数据");
    }

    public void setHasMore(boolean hasMore){
        this.mHasMore = hasMore;
    }

    public boolean hasMore(){
        return mHasMore;
    }

    /**
     * set loadMoreFooter Text
     * @param text
     */
    public void setLoadMoreText(String text){
        if (mLvFooter != null) {
            mLvFooter.setText(text);
        }
    }

    public void setLoadMoreImage(int resId){
        if (mLvFooter != null) {
            mLvFooter.setImageResourceId(resId);
        }
    }

    public void setLoadMoreImageAnim(int resId){
        if (mLvFooter != null) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), resId);
            animation.setInterpolator(new LinearInterpolator());
            mLvFooter.getImageView().startAnimation(animation);
        }
    }

    /**
     * create footView
     * @return
     */
    protected ListLoadingMoreFooter newFooterView(){
        return new ListLoadingMoreFooter(getActivity());
    }

    /**
     * create swipeRefreshLayout
     * @return
     */
    protected SwipeRefreshLayout newSwipeRefreshLayout() {
        return new SwipeRefreshLayout(getActivity());
    }

    /**
     * create loadMoreListView
     * @return
     */
    protected LoadMoreListView newLoadMoreListView(){
        LoadMoreListView listView = new LoadMoreListView(getActivity());
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setDividerHeight(0);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        return listView;
    }

    /**
     * 是否是合法的listView
     * @return
     */
    protected boolean isValid() {
        return mListAdapter != null;
    }

    protected abstract SingleTypeAdapter<K> getListAdapter();
    public abstract List<K> getList(BaseObject result);
}
