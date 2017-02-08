package com.hyena.framework.samples.animator.render.node;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/27.
 */
public class TitleNode extends CNode {

    private Bitmap mBackGround;
    private Bitmap mStarBitmap;
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String mTitle;

    private String mSubTitleLeft;
    private String mSubTitleRight;

    private int mTitleColor;
    private int mSubTitleLeftColor;
    private int mSubTitleRightColor;

    private int mTitleFontSize = 11;
    private int mSubTitleFontSize = 11;

    public static TitleNode create(Director director) {
        return new TitleNode(director);
    }

    private TitleNode(Director director) {
        super(director);
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setSubTitle(String leftTxt, String rightTxt) {
        this.mSubTitleLeft = leftTxt;
        this.mSubTitleRight = rightTxt;
    }

    public void setTitleStyle(int fontSize, String fontColor) {
        this.mTitleFontSize = fontSize;
        this.mTitleColor = Color.parseColor(fontColor);
    }

    public void setSubTitleStyle(int fontSize, String leftColor, String rightColor) {
        this.mSubTitleFontSize = fontSize;
        this.mSubTitleLeftColor = Color.parseColor(leftColor);
        this.mSubTitleRightColor = Color.parseColor(rightColor);
    }

    public void setBackGround(Bitmap bitmap) {
        this.mBackGround = bitmap;
    }

    public void setStarBitmap(Bitmap bitmap) {
        this.mStarBitmap = bitmap;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        mRect.set(getPosition().x, getPosition().y,
                getPosition().x + getWidth(), getPosition().y + getHeight());

        if (mBackGround != null && !mBackGround.isRecycled()) {
            canvas.drawBitmap(mBackGround, null, mRect, mPaint);
        }
        mPaint.setColor(mTitleColor);
        mPaint.setTextSize(mTitleFontSize);
        int x = (int) (mRect.left + (mRect.width() - mPaint.measureText(mTitle)) / 2);
        int y = mRect.top + getTextHeight(mPaint);
        if (!TextUtils.isEmpty(mTitle))
            canvas.drawText(mTitle, x, y, mPaint);

        mPaint.setTextSize(mSubTitleFontSize);
        mPaint.setColor(mSubTitleLeftColor);
        x = (int) (mRect.left + (mRect.width() -
                mPaint.measureText(mSubTitleLeft + mSubTitleRight)) / 2);
        y += getTextHeight(mPaint);
        if (!TextUtils.isEmpty(mSubTitleLeft)) {
            canvas.drawText(mSubTitleLeft, x, y, mPaint);
            x += mPaint.measureText(mSubTitleLeft);
        }
        mPaint.setColor(mSubTitleRightColor);
        if (!TextUtils.isEmpty(mSubTitleRight)) {
            canvas.drawText(mSubTitleRight, x, y, mPaint);
            x += mPaint.measureText(mSubTitleRight);
        }

        if (mStarBitmap != null && !mStarBitmap.isRecycled()) {
            canvas.drawBitmap(mStarBitmap, x + UIUtils.dip2px(2), y - mStarBitmap.getHeight() - UIUtils.dip2px(2), mPaint);
        }
    }

    private int getTextHeight(Paint paint) {
        if (paint != null) {
            Paint.FontMetrics fm = paint.getFontMetrics();
            return (int) (fm.bottom - fm.top);
        }
        return 0;
    }
}
