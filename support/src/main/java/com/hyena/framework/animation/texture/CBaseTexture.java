package com.hyena.framework.animation.texture;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;

import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 基础纹理
 *
 * @author yangzc
 */
public class CBaseTexture extends CNode {

    Matrix mMatrix;
    Paint mPaint;

    int mAlpha = 255;
    float mInitScaleX = 1;
    float mInitScaleY = 1;
    float mScaleX = 1;
    float mScaleY = 1;
    float mAnchorX = 0.0f;
    float mAnchorY = 0.0f;
    float mDegrees = 0;
    float mSkewX = 0f;
    float mSkewY = 0f;
//	int mX = 0;
//	int mY = 0;

    public CBaseTexture(Director director) {
        super(director);
        initTexture();
    }

    /**
     * 初始化Texture
     */
    private void initTexture() {
        mMatrix = new Matrix();
        mPaint = new Paint();
    }

    /**
     * 设置alpha
     *
     * @param alpha
     */
    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        invalidate();
    }

    /**
     * 获得透明度
     *
     * @return
     */
    public int getAlpha() {
        return mAlpha;
    }

    /**
     * 初始缩放比
     * @param sx
     * @param sy
     */
    public void setInitScale(float sx, float sy){
        this.mInitScaleX = sx;
        this.mInitScaleY = sy;
        invalidate();
    }

    /**
     * 缩放图片
     *
     * @param sx
     * @param sy
     */
    public void setScale(float sx, float sy) {
        this.mScaleX = sx;
        this.mScaleY = sy;
        invalidate();
    }

    /**
     * 设置锚点
     *
     * @param x
     * @param y
     */
    public void setAnchor(float x, float y) {
        this.mAnchorX = x;
        this.mAnchorY = y;
        invalidate();
    }

    /**
     * 设置倾斜角度
     *
     * @param skewX
     * @param skewY
     */
    public void setSkew(float skewX, float skewY) {
        this.mSkewX = skewX;
        this.mSkewY = skewY;
    }

    /**
     * 旋转
     *
     * @param degrees
     */
    public void rotate(float degrees) {
        this.mDegrees = degrees;
        invalidate();
    }

    /**
     * 设置位置
     * @param x
     * @param y
     */
//	public void setPosition(int x, int y){
//		this.mX = x;
//		this.mY = y;
//		invalidate();
//	}

    /**
     * 更新数据
     */
    private void invalidate() {
        if (mPaint == null)
            mPaint = new Paint();
        mPaint.reset();
        mPaint.setAlpha(mAlpha);

        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mMatrix.reset();
//		mMatrix.postTranslate(mX, mY);//设置位置

        mMatrix.preScale(mInitScaleX, mInitScaleY, 0, 0);

        int anchorX = 0;
        if (mInitScaleX != 0) {
            anchorX = (int) (getWidth() * mAnchorX);
        }
        int anchorY = 0;
        if (mInitScaleY != 0) {
            anchorY = (int) (getHeight() * mAnchorY);
        }

        mMatrix.postScale(mScaleX, mScaleY, anchorX, anchorY);//缩放
        mMatrix.postRotate(mDegrees, anchorX, anchorY);//旋转
        mMatrix.postSkew(mSkewX, mSkewY, anchorX, anchorY);//视野
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    /**
     * 初始化变量
     */
    public void reset() {
        this.mAlpha = 255;
        this.mScaleX = 1;
        this.mScaleY = 1;
        this.mAnchorX = 0.0f;
        this.mAnchorY = 0.0f;
        this.mDegrees = 0f;
//		this.mX = 0;
//		this.mY = 0;
        invalidate();
    }

}
