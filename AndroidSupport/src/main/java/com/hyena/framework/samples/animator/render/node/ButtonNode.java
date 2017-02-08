package com.hyena.framework.samples.animator.render.node;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/27.
 */
public class ButtonNode extends CNode {

    private String mTitle;
    private int mFontSize = UIUtils.dip2px(17);
    private int mFontColor = Color.WHITE;

    private int mBackGroundColor =0xffde5b66;
    private float mRx, mRy;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ButtonNode(Director director) {
        super(director);
        init();
    }

    public static ButtonNode create(Director director) {
        return new ButtonNode(director);
    }

    private void init(){
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setTitleStyle(int fontSize, String fontColor) {
        this.mFontSize = fontSize;
        this.mFontColor = Color.parseColor(fontColor);
    }

    public void setBackGroundColor(String color) {
        this.mBackGroundColor = Color.parseColor(color);
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return super.onTouch(event);
    }

    private RectF mRect = new RectF();

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        mRect.set(getPosition().x, getPosition().y, getPosition().x + getWidth(),
                getPosition().y + getHeight());

        mRx = mRect.height()/2;
        mRy = mRx;

        //draw background
        mPaint.setColor(mBackGroundColor);
        canvas.drawRoundRect(mRect, mRx, mRy, mPaint);
        //draw title
        if (!TextUtils.isEmpty(mTitle)) {
            mPaint.setColor(mFontColor);
            mPaint.setTextSize(mFontSize);
            int x = getPosition().x + (getWidth() - getTextWidth()) / 2;
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            int y = (int) (getPosition().y + (getHeight() - fm.bottom - fm.top) / 2);
            canvas.drawText(mTitle, x, y, mPaint);
        }
    }

    private int getTextWidth(){
        if (mPaint != null && !TextUtils.isEmpty(mTitle)) {
            return (int) mPaint.measureText(mTitle);
        }
        return 0;
    }
}
