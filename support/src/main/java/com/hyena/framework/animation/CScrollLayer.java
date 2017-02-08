package com.hyena.framework.animation;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * Created by yangzc on 16/4/19.
 */
public class CScrollLayer extends CLayer {

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private float mTouchSlop = 0;
    private float mMinimumVelocity = 0;
    private float mMaximumVelocity = 0;

    private boolean mDragging = false;
    private float mLastX, mLastY;

    public static CScrollLayer create(Director director) {
        return new CScrollLayer(director);
    }

    protected CScrollLayer(Director director) {
        super(director);
        init();
    }

    private void init() {
        mScroller = new Scroller(getDirector().getContext(), new DecelerateInterpolator());
        final ViewConfiguration configuration = ViewConfiguration.get(getDirector().getContext());
        mTouchSlop = configuration.getScaledTouchSlop();//手势滑动距离
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();//fling动作最小速度
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();//fling动作最大速度
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScrollable() || !isTouchable()) {
            return super.onInterceptTouchEvent(ev);
        }
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE && mDragging) {
            return true;
        }
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDragging = false;
                abortScroller();
                initOrResetVelocityTracker();
                addTrackerMovement(ev);

                mLastX = x;
                mLastY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mDragging || (Math.abs(y - mLastY) > Math.abs(x - mLastX)
                        && Math.abs(y - mLastY) > mTouchSlop)) {
                    mDragging = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
        return mDragging;
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        if (!isScrollable() || !isTouchable()) {
            return super.onTouch(event);
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastX = x;
                mLastY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                addTrackerMovement(event);
                int minY = -getHeight() + getDirector().getViewSize().height();
                if (getScrollY() + y - mLastY < minY) {
                    scrollTo(0, minY);
                } else if (getScrollY() + y - mLastY > 0) {
                    scrollTo(0, 0);
                } else {
                    int dy = (int) (y - mLastY);
                    scrollBy(0, dy);
                    mLastY = y;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mDragging = false;

                addTrackerMovement(event);
                abortScroller();
                if (mVelocityTracker != null) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    float yVelocity = mVelocityTracker.getYVelocity();
                    if (Math.abs(yVelocity) > mMinimumVelocity) {
                        int minY = -getHeight() + getDirector().getViewSize().height();
                        mScroller.fling(0, getScrollY(), 0, (int) yVelocity
                                , 0, 0, minY, 0);
                    }
                }
                recycleTracker();
                break;
            }
        }
        return true;
    }

    @Override
    public synchronized void update(float dt) {
        super.update(dt);
        if (mScroller != null && mScroller.computeScrollOffset()) {
            int y = mScroller.getCurrY();
            int minY = -getHeight() + getDirector().getViewSize().height();
            if (y < minY)
                y = minY;
            if (y > 0)
                y = 0;
            scrollTo(0, y);
        }
    }

    private void abortScroller() {
        if (mScroller != null && !mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    private void addTrackerMovement(MotionEvent event) {
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void recycleTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
