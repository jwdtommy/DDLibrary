/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView Wrapper
 *
 * @author yangzc
 */
public class SimpleRecycleView extends RecyclerView {

    private List<View> mHeaders = new ArrayList<View>();
    private List<View> mFooters = new ArrayList<View>();

    public SimpleRecycleView(Context context, AttributeSet attrs, int arg2) {
        super(context, attrs, arg2);
        init();
    }

    public SimpleRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleRecycleView(Context context) {
        super(context);
        init();
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    @Override
    public void setItemAnimator(ItemAnimator animator) {
        super.setItemAnimator(animator);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (direction > 0) {//向下
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            int count = getAdapter().getItemCount();
            if (layoutManager instanceof LinearLayoutManager && count > 0) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    return false;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastItems = new int[4];
                staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastItems);
                int lastItem = max(lastItems);
                if (lastItem == count - 1) {
                    return false;
                }
            }
            return true;
        } else {//向上
            if (Build.VERSION.SDK_INT < 14) {
                return getScaleY() > 0;
            } else {
                return super.canScrollVertically(direction);
            }
        }
    }

    private void init() {
    }

    private int max(int[] a) {
        // 返回数组最大值
        int x;
        int aa[] = new int[a.length];
        System.arraycopy(a, 0, aa, 0, a.length);
        x = aa[0];
        for (int i = 1; i < aa.length; i++) {
            if (aa[i] > x) {
                x = aa[i];
            }
        }
        return x;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!mHeaders.isEmpty() || !mFooters.isEmpty()) {
            super.setAdapter(new InternalAdapter(adapter, mHeaders, mFooters));
        } else {
            super.setAdapter(adapter);
        }
    }

    public void addHeader(View header) {
        mHeaders.add(header);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void removeHeader(View header) {
        mHeaders.remove(header);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getHeaderCount() {
        if (mHeaders != null)
            return mHeaders.size();
        return 0;
    }

    public void addFooter(View footer) {
        mFooters.add(footer);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void removeFooter(View footer) {
        mFooters.remove(footer);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getFooterCount() {
        if (mFooters != null)
            return mFooters.size();
        return 0;
    }

    private class InternalAdapter extends RecyclerView.Adapter {

        private List<View> mHeaders;
        private List<View> mFooters;
        private Adapter mAdapter;

        public InternalAdapter(Adapter adapter, List<View> headers, List<View> footers) {
            this.mAdapter = adapter;
            this.mHeaders = headers;
            this.mFooters = footers;
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (viewType < getHeaderCount()) {
                //header
                View header = mHeaders.get(viewType);
                return new SingleViewHolder(header);
            } else if (viewType < getHeaderCount() + getFooterCount()) {
                //footer
                View footer = mFooters.get(viewType - getHeaderCount());
                return new SingleViewHolder(footer);
            } else {
                return mAdapter.onCreateViewHolder(viewGroup, viewType - getHeaderCount());
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            if (position < getHeaderCount()) {
                //header
            } else if (position < getHeaderCount() + mAdapter.getItemCount()) {
                mAdapter.onBindViewHolder(viewHolder, position - getHeaderCount());
            } else {
                //footer
            }
        }

        @Override
        public int getItemViewType(int position) {
            int headerCnt = getHeaderCount();
            int footerCnt = getFooterCount();
            if (position < headerCnt) {
                return position;
            } else if (position < headerCnt + mAdapter.getItemCount()) {
                return mAdapter.getItemViewType(position) + headerCnt + footerCnt;
            } else {
                return position - mAdapter.getItemCount();
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            if (mHeaders != null)
                itemCount += mHeaders.size();
            if (mFooters != null)
                itemCount += mFooters.size();
            if (mAdapter != null)
                itemCount += mAdapter.getItemCount();
            return itemCount;
        }

        private int getHeaderCount() {
            if (mHeaders != null) {
                return mHeaders.size();
            }
            return 0;
        }

        private int getFooterCount() {
            if (mFooters != null) {
                return mFooters.size();
            }
            return 0;
        }

        private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition + getHeaderCount(), toPosition + getHeaderCount());
            }
        };
    }

    private class SingleViewHolder extends RecyclerView.ViewHolder {

        public SingleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
