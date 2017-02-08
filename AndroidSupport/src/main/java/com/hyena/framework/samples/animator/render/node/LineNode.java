package com.hyena.framework.samples.animator.render.node;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.animation.sprite.CPoint;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/22.
 */
public class LineNode extends CNode {

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_DOT = 1;

    private int mDistance;
    private int mRadius;

    private Paint mPaint;
    private CPoint mStartPoint;
    private CPoint mEndPoint;
    private int mStyle = STYLE_NORMAL;

    public static LineNode create(Director director){
        return new LineNode(director);
    }

    private LineNode(Director director) {
        super(director);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDistance = UIUtils.dip2px(5);
        mRadius = UIUtils.dip2px(1.25f);
    }

    public void setColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public Point getPosition() {
        int x = mStartPoint.mX, y = mStartPoint.mY;
        if (x > mEndPoint.mX) {
            x = mEndPoint.mX;
        }
        if (y > mEndPoint.mY) {
            y = mEndPoint.mY;
        }
        super.getPosition().set(x, y);
        return super.getPosition();
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return super.onTouch(event);
    }

    public void setStartPoint(CPoint start) {
        this.mStartPoint = start;
    }

    public void setEndPoint(CPoint end) {
        this.mEndPoint = end;
    }

    public void setStyle(int style) {
        this.mStyle = style;
    }

    @Override
    public int getWidth() {
        return Math.abs(mStartPoint.mX - mEndPoint.mX);
    }

    @Override
    public int getHeight() {
        return Math.abs(mStartPoint.mY - mEndPoint.mY);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (mStartPoint != null && mEndPoint != null) {
            switch (mStyle) {
                case STYLE_NORMAL: {
                    canvas.drawLine(mStartPoint.mX, mStartPoint.mY, mEndPoint.mX, mEndPoint.mY, mPaint);
                    break;
                }
                case STYLE_DOT: {
                    CPoint mTempPoint;
                    if (mEndPoint.mY < mStartPoint.mY) {
                        mTempPoint = mEndPoint;
                        mEndPoint = mStartPoint;
                        mStartPoint = mTempPoint;
                    }

                    int y = mStartPoint.mY;
                    while (y < mEndPoint.mY) {
                        y += mDistance;
                        int cx = mStartPoint.mX + (mEndPoint.mX - mStartPoint.mX) * (y - mStartPoint.mY) / (mEndPoint.mY - mStartPoint.mY);
                        canvas.drawCircle(cx, y, mRadius, mPaint);
                    }
                    break;
                }
            }
        }
    }
}
