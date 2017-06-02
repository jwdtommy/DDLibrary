package com.dd.news.widgets.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
/**
 * 自由拖拽布局
 * Created by J.Tommy on 17/4/14.
 */
public class DragGroupView extends FrameLayout {
    private ViewDragHelper mViewDragHelper;
    private OnFillInBlankListener mOnFillInBlankListener;

    private PathEffect mPathEffect;
    private Path mPath;
    private PathMeasure mPathMeasure;
    private PathDashPathEffect mPathDashPathEffect;
    private Paint mPathPaint;
    public DragGroupView(Context context) {
        super(context);
        init();
    }

    public DragGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPathPaint=new Paint();
        mPathPaint.setStrokeWidth(30);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(Color.BLUE);
        mPathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST));
        mPath=new Path();
        mPath.lineTo(200,200);
        mPath.lineTo(300,500);
        mPath.lineTo(500,700);

        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                Log.i("jwd", "... tryCaptureView");
                if (child instanceof Draggable) {
                    return true;
                }
                return false;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                DragBlankView dragBlankView = findIntersectBlankView((DragItemView) releasedChild);
                if (dragBlankView != null) {
                    mViewDragHelper.smoothSlideViewTo(releasedChild, dragBlankView.getLeft(), dragBlankView.getTop());
                    invalidate();
                    if (mOnFillInBlankListener != null) {
                        mOnFillInBlankListener.onFillInBlank((DragItemView) releasedChild);
                    }
                } else {
                    mViewDragHelper.smoothSlideViewTo(releasedChild, ((DragItemView) releasedChild).getSrcLeft(), ((DragItemView) releasedChild).getSrcTop());
                    invalidate();
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                mPath.lineTo(left,top);
                invalidate();
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - child.getWidth();
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound=getPaddingTop();
                final int bottomBound=getHeight()-child.getHeight();
                final int newTop=Math.min(Math.max(top,topBound),bottomBound);
                return newTop;
            }
        });
    }

    public void setOnFillInBlankListener(OnFillInBlankListener onFillInBlankListener) {
        mOnFillInBlankListener = onFillInBlankListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPathPaint);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private DragBlankView findIntersectBlankView(DragItemView currentDragItemView) {
        Rect rectCurrentDrag = new Rect();
        currentDragItemView.getGlobalVisibleRect(rectCurrentDrag);
        Rect rectBlank = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof Fixedable) {
                view.getGlobalVisibleRect(rectBlank);
                if (rectCurrentDrag.intersect(rectBlank)) {
                    return (DragBlankView) view;
                }
            }
        }
        return null;
    }


    public static interface OnFillInBlankListener {
        void onFillInBlank(DragItemView dragItemView);
    }
}
