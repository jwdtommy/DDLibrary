package com.hyena.framework.app.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.hyena.framework.R;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.debug.InvokeHelper;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by yangzc on 16/9/14.
 */

public class RefreshableLayout extends RelativeLayout {

    private static final int MODE_PULL_FROM_NONE = 0;
    private static final int MODE_PULL_FROM_START = 1;
    private static final int MODE_PULL_FROM_END = 2;

    private static final int MAX_MOVE_DISTANCE = UIUtils.dip2px(120);

    private View mTarget = null;
    private View mScrollerView = null;

    private int mTouchSlop = 0;
    private float mInitialDownY = -1;
    private float mInitialMotionY = -1;
    private boolean mIsBeingDragged;
    private int mCurrentMode = MODE_PULL_FROM_NONE;

    private boolean mRefreshing, mLoadingMore;

    private boolean mEnableRefresh = true, mEnableLoadMore = true;
    private AbsRefreshablePanel mHeaderPanel = null;
    private AbsRefreshablePanel mFooterPanel = null;

    public RefreshableLayout(Context context) {
        super(context);
        init();
    }

    public RefreshableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        relayout();
    }

    public void setEnableRefresh(boolean enableRefresh) {
        this.mEnableRefresh = enableRefresh;
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        this.mEnableLoadMore = enableLoadMore;
    }

    private void relayout() {
        relayoutHeader();
        relayoutFooter();
    }

    private void relayoutHeader() {
        int paddingTop = 0;
        if (mHeaderPanel != null) {
            LayoutParams headerParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mHeaderPanel.getContentHeight());
            headerParams.addRule(ALIGN_PARENT_TOP);
            mHeaderPanel.setId(R.id.refresh_header);
            addView(mHeaderPanel, headerParams);
            paddingTop = -mHeaderPanel.getContentHeight();
        }
        setPadding(0, paddingTop, 0, getPaddingBottom());
    }

    private void relayoutFooter() {
        int paddingBottom = 0;
        if (mFooterPanel != null) {
            LayoutParams footerParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mFooterPanel.getContentHeight());
            footerParams.addRule(ALIGN_PARENT_BOTTOM);
            mFooterPanel.setId(R.id.refresh_footer);
            addView(mFooterPanel, footerParams);
            paddingBottom = -mFooterPanel.getContentHeight();
        }
        setPadding(0, getPaddingTop(), 0, paddingBottom);
    }

    public void setHeaderPanel(AbsRefreshablePanel headerPanel) {
        if (mHeaderPanel != null) {
            removeView(mHeaderPanel);
        }
        this.mHeaderPanel = headerPanel;
        relayoutHeader();
    }

    public void setFooterPanel(AbsRefreshablePanel footerPanel) {
        if (mFooterPanel != null) {
            removeView(mFooterPanel);
        }
        this.mFooterPanel = footerPanel;
        relayoutFooter();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mTarget == null)
            insureTarget();

        if (mTarget != null) {
            LayoutParams params = (LayoutParams) mTarget.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.refresh_header);
            params.addRule(RelativeLayout.ABOVE, R.id.refresh_footer);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        if (mTarget == null)
            insureTarget();

        if (mTarget != null) {
            LayoutParams targetParams = (LayoutParams) mTarget.getLayoutParams();
            targetParams.addRule(RelativeLayout.BELOW, R.id.refresh_header);
            targetParams.addRule(RelativeLayout.ABOVE, R.id.refresh_footer);
        }
    }

    private void insureTarget() {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof AbsRefreshablePanel)) {
                    mTarget = child;
                    break;
                }
            }
        }

        insureScrollView();
    }

    private void insureScrollView() {
        if (mScrollerView == null) {
            if (mTarget != null && mTarget instanceof SwipeRefreshLayout) {
                mScrollerView = (View) InvokeHelper.getFieldValue(mTarget, "mTarget");
            } else {
                mScrollerView = mTarget;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        insureScrollView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        insureScrollView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mRefreshing || mLoadingMore || (mHeaderPanel == null && mFooterPanel == null)
                || (!mEnableRefresh && !mEnableLoadMore)) {
            return false;
        }

        if (mTarget == null) {
            insureTarget();
        }
        insureScrollView();

        if (mTarget != null && mTarget instanceof SwipeRefreshLayout) {
            if (((SwipeRefreshLayout) mTarget).isRefreshing())
                return false;
        }

        int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                mInitialDownY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float y = ev.getY();
                float yDiff = y - mInitialDownY;
                if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
                    if (mHeaderPanel != null && mEnableRefresh && !canChildScrollUp() && yDiff > 1) {
                        //top
                        mCurrentMode = MODE_PULL_FROM_START;
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mIsBeingDragged = true;
                    } else if (mFooterPanel != null && mEnableLoadMore && !canChildScrollDown() && yDiff < -1) {
                        //bottom
                        mCurrentMode = MODE_PULL_FROM_END;
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mIsBeingDragged = true;
                    } else {
                        //NO-OP
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                break;
            }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRefreshing || mLoadingMore || (mHeaderPanel == null && mFooterPanel == null)
                || (!mEnableRefresh && !mEnableLoadMore))
            return false;

        if (mTarget != null && mTarget instanceof SwipeRefreshLayout) {
            if (((SwipeRefreshLayout) mTarget).isRefreshing())
                return false;
        }

        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float y = event.getY();
                float overScroll = (mInitialMotionY - y) * .5f;
                if (mIsBeingDragged) {
                    if (mCurrentMode == MODE_PULL_FROM_START) {
                        overScroll = Math.max(Math.min(0, overScroll), -MAX_MOVE_DISTANCE);
                    } else if (mCurrentMode == MODE_PULL_FROM_END) {
                        overScroll = Math.min(Math.max(0, overScroll), MAX_MOVE_DISTANCE);
                    } else {
                        overScroll = 0;
                    }
                    moveTarget(overScroll);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                moveRelease();
                break;
            }
        }
        return true;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((Build.VERSION.SDK_INT < 21 && mScrollerView instanceof AbsListView)
                || (mScrollerView != null && !ViewCompat.isNestedScrollingEnabled(mScrollerView))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    public void setRefreshing(boolean isRefreshing) {
        if (mHeaderPanel != null) {
            this.mRefreshing = isRefreshing;
            mCurrentMode = MODE_PULL_FROM_START;
            if (isRefreshing) {
                scroll(0, -mHeaderPanel.getContentHeight());
                mHeaderPanel.setStatus(AbsRefreshablePanel.STATUS_REFRESH);
            } else {
                scroll(getScrollY(), 0);
                mHeaderPanel.setStatus(AbsRefreshablePanel.STATUS_RESET);
            }
        }
    }

    public void setLoadingMore(boolean isLoading) {
        if (mFooterPanel != null) {
            this.mLoadingMore = isLoading;
            mCurrentMode = MODE_PULL_FROM_END;
            if (isLoading) {
                scroll(0, mFooterPanel.getContentHeight());
                mFooterPanel.setStatus(AbsRefreshablePanel.STATUS_REFRESH);
            } else {
                scroll(getScrollY(), 0);
                mFooterPanel.setStatus(AbsRefreshablePanel.STATUS_RESET);
            }
        }
    }

    /**
     * 手势释放
     */
    private void moveRelease() {
        AbsRefreshablePanel pullItem = null;
        int toScrollY = 0;
        if (mCurrentMode == MODE_PULL_FROM_START) {
            pullItem = mHeaderPanel;
            toScrollY = -pullItem.getContentHeight();
        } else if (mCurrentMode == MODE_PULL_FROM_END) {
            pullItem = mFooterPanel;
            toScrollY = pullItem.getContentHeight();
        }

        if (pullItem != null) {
            if (Math.abs(getScrollY()) >= pullItem.getContentHeight()) {
                pullItem.setStatus(AbsRefreshablePanel.STATUS_REFRESH);
                scroll(getScrollY(), toScrollY);
                if (mCurrentMode == MODE_PULL_FROM_START) {
                    mRefreshing = true;
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                } else if (mCurrentMode == MODE_PULL_FROM_END) {
                    mLoadingMore = true;
                    if (mRefreshListener != null) {
                        mRefreshListener.onLoadMore();
                    }
                }
            } else {
                scroll(getScrollY(), 0);
            }
        }
    }

    /**
     * 手势拖动
     */
    private void moveTarget(float overScroll) {
        scrollTo(0, (int) overScroll);
        AbsRefreshablePanel pullItem = null;
        if (mCurrentMode == MODE_PULL_FROM_START) {
            pullItem = mHeaderPanel;
        } else if (mCurrentMode == MODE_PULL_FROM_END) {
            pullItem = mFooterPanel;
        }

        if (pullItem != null) {
            pullItem.setScrolling(overScroll, pullItem.getContentHeight());
            if (Math.abs(overScroll) >= pullItem.getContentHeight()) {
                pullItem.setStatus(AbsRefreshablePanel.STATUS_READY_REFRESH);
            } else {
                pullItem.setStatus(AbsRefreshablePanel.STATUS_START_PULL);
            }
        }
    }

    /**
     * 滚动回退
     *
     * @param fromScroll 开始位置
     * @param toScroll   结束位置
     */
    private void scroll(final float fromScroll, final float toScroll) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                scrollTo(0, (int) fromScroll);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                scrollTo(0, (int) toScroll);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                scrollTo(0, (int) toScroll);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                float newScrollValue = value * (toScroll - fromScroll) + fromScroll;
                scrollTo(0, (int) newScrollValue);
            }
        };
        animator.addUpdateListener(listener);
        animator.addListener(listener);
        animator.start();
    }

    /**
     * 是否可以向上滚动
     */
    private boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mScrollerView instanceof AbsListView) {
                AbsListView listView = (AbsListView) mScrollerView;
                return listView.getChildCount() > 0
                        && (listView.getFirstVisiblePosition() > 0
                        || listView.getChildAt(0).getTop() < listView.getPaddingTop());
            } else {
                return mScrollerView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mScrollerView, -1);
        }
    }

    /**
     * 是否可以向下滚动
     */
    private boolean canChildScrollDown() {
        if (!canChildScrollUp()) {//less data
            return true;
        }
        if (mScrollerView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mScrollerView;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount();
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
        } else if (mScrollerView instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mScrollerView;
            int count = absListView.getAdapter().getCount();
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            if (firstVisiblePosition == 0
                    && absListView.getChildAt(0).getTop() >= absListView
                    .getPaddingTop()) {
                return false;
            }
            int lastPos = absListView.getLastVisiblePosition();
            return lastPos > 0 && count > 0 && lastPos == count - 1;
        } else if (mScrollerView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mScrollerView;
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (view != null) {
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff == 0) {
                    return false;
                }
            }
        } else {
            return ViewCompat.canScrollVertically(mScrollerView, 1);
        }
        return true;
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

    private OnRefreshListener mRefreshListener;

    public void setRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public static interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();
    }
}
