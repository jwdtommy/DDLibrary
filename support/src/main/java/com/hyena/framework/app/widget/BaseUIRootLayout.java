package com.hyena.framework.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.AnimationUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * BaseUIFragment root viewGroup
 * Created by yangzc on 16/2/26.
 */
public class BaseUIRootLayout extends RelativeLayout {

    private int mCenterX, mCenterY;

    private int mRadius = 1;
    private int mRadiusEnd = mRadius;

    private ValueAnimator mAnimator;
    private Path mPath = new Path();
    private boolean mShowCover = false;

    public BaseUIRootLayout(Context context) {
        super(context);
    }

    public BaseUIRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseUIRootLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void playCoverScale(int centerX, int centerY) {
        this.mCenterX = centerX;
        this.mCenterY = centerY;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int leftTopDis = mCenterX * mCenterX + mCenterY * mCenterY;
        int rightTopDis = (width - mCenterX)*(width - mCenterX) + mCenterY * mCenterY;
        int leftBottomDis = mCenterX * mCenterX + (height - mCenterY)*(height - mCenterY);
        int rightBottomDis = (width - mCenterX)*(width - mCenterX) + (height - mCenterY)*(height - mCenterY);

        double endRadius = leftTopDis;
        if (rightTopDis > endRadius) {
            endRadius = rightTopDis;
        }
        if (leftBottomDis > endRadius) {
            endRadius = leftBottomDis;
        }
        if (rightBottomDis > endRadius) {
            endRadius = rightBottomDis;
        }
        mRadiusEnd = (int) Math.sqrt(endRadius);

        mAnimator = ValueAnimator.ofFloat(0, 1.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(1000);
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                mShowCover = true;
                setWillNotDraw(false);
                postInvalidate();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mShowCover = false;
                setWillNotDraw(true);
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                mRadius = (int) (mRadiusEnd * value);
                LogUtil.v("yangzc", "mRadius: " + mRadius);
                postInvalidate();
            }
        };
        mAnimator.addListener(listener);
        mAnimator.addUpdateListener(listener);
        mAnimator.start();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mShowCover) {
            mPath.reset();
            canvas.save();
            LogUtil.v("yangzc", "onDraw: " + mRadius);
            mPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);
            canvas.clipPath(mPath);
            super.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

}
