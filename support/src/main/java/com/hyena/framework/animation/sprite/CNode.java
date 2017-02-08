package com.hyena.framework.animation.sprite;

import java.util.Random;

import com.hyena.framework.animation.CLayer;
import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.RenderView;
import com.hyena.framework.utils.UIUtils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * 单个对象节点
 *
 * @author yangzc
 */
public abstract class CNode {

    public static final int FILL_PARENT = -1;

    private CNode mParent;

    //z轴索引
    private int mZIndex;
    private String mId;
    private String mTag;
    private int mX = 0, mY = 0;
    private int mWidth = FILL_PARENT;
    private int mHeight = FILL_PARENT;
    private boolean isVisible = true;

    private Paint mPaint;
    //对齐方式
    protected CAlign mAlign = null;
    //花费的时间
    private float elapsed = 0;
    private Point mPosition = new Point();
    private static Random mRandom = new Random();

    private Director mDirector;

    public CNode(Director director) {
        this.mDirector = director;
    }

    public void setParent(CNode parent) {
        this.mParent = parent;
    }

    /**
     * 渲染
     *
     * @param canvas
     */
    public void render(Canvas canvas) {
//        if (!isValid() || !isVisible()) {
//            return;
//        }
//        if (mPaint != null) {
//            canvas.drawRect(new Rect(getPosition().x, getPosition().y,
//                    getPosition().x + getWidth(), getPosition().y + getHeight()), mPaint);
//        }
    }

    /**
     * 刷新帧
     *
     * @param dt 刷新间隔
     */
    public void update(float dt) {
//		LogUtil.v(LOG_TAG, "update");
        elapsed += dt;
    }

    /**
     * 获得已经使用时间
     *
     * @return
     */
    protected float getElapsed() {
        return elapsed;
    }

    /**
     * 重置状态
     */
    public void reset() {
        elapsed = 0f;
    }

    /**
     * @return the isVisible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * @param isVisible the isVisible to set
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * 设置节点位置
     *
     * @param position
     */
    public void setPosition(Point position) {
        this.mX = position.x;
        this.mY = position.y;
        updatePosition();
    }

    /**
     * 设置view大小
     *
     * @param width
     * @param height
     */
    public void setViewSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    /**
     * 内容宽度
     *
     * @return
     */
    public int getWidth() {
        if (mWidth > 0)
            return mWidth;

        if (mParent != null) {
            if (mWidth == FILL_PARENT) {
                mWidth = mParent.getWidth();
            }
            return mWidth;
        }
        if (mDirector == null || mDirector.getViewSize() == null)
            return 0;

        return mDirector.getViewSize().width();
    }

    /**
     * 内容高度
     *
     * @return
     */
    public int getHeight() {
        if (mHeight > 0) {
            return mHeight;
        }

        if (mParent != null) {
            if (mHeight == FILL_PARENT) {
                mHeight = mParent.getHeight();
            }
            return mHeight;
        }
        if (mDirector == null || mDirector.getViewSize() == null) {
            return 0;
        }
        return mDirector.getViewSize().height();
    }

    /**
     * 设置对齐方式
     *
     * @param align`
     */
    public void setAlign(CAlign align) {
        if (align == null)
            return;
        this.mAlign = align;
        updatePosition();
    }

    public enum CAlign {
        TOP_LEFT(0), TOP_CENTER(1), TOP_RIGHT(2), CENTER_LEFT(3), CENTER_CENTER(4),
        CENTER_RIGHT(5), BOTTOM_LEFT(6), BOTTOM_CENTER(7), BOTTOM_RIGHT(8);

        CAlign(int type) {
            this.type = type;
        }

        private int type;

        public int getValue() {
            return type;
        }
    }

    /**
     * 设置z轴索引
     *
     * @param zIndex
     */
    public void setZIndex(int zIndex) {
        this.mZIndex = zIndex;
    }

    /**
     * 获得z轴索引
     *
     * @return
     */
    public int getZIndex() {
        return mZIndex;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return onTouch(ev);
    }

    private Rect mTemp = new Rect();
    private boolean mTouching = false;

    public boolean onTouch(MotionEvent event) {
        if (mClickListener == null) {
            return false;
        }
        int scrollX = 0, scrollY = 0;
        if (mParent != null && mParent instanceof CLayer) {
            scrollX = ((CLayer) mParent).getScrollX();
            scrollY = ((CLayer) mParent).getScrollY();
        }

        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mTemp.set(getPosition().x, getPosition().y, getPosition().x + getWidth(),
                        getPosition().y + getHeight());
                mTouching = mTemp.contains(x + scrollX, y - scrollY);
                if (mTouching) {
                    onTouchDown();
                }
                return mTouching;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mTemp.contains(x + scrollX, y - scrollY)) {
                    //click
                    if (mClickListener != null) {
                        mClickListener.onClick(this);
                    }
                }
                onTouchUp();
                mTouching = false;
                break;
            }
        }
        return mTouching;
    }

    /**
     * 获得随机数对象
     *
     * @return
     */
    public static Random getRandomObj() {
        if (mRandom == null)
            mRandom = new Random();
        return mRandom;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        return UIUtils.dip2px(dpValue);
    }

    /**
     * 获得节点位置
     *
     * @return
     */
    public Point getPosition() {
        if (mDirector == null || mDirector.getViewSize() == null) {
            mPosition.set(mX, mY);
            return mPosition;
        }
        return mPosition;
    }

    /**
     * 更新位置
     */
    private void updatePosition() {
        int pLeft = 0, pTop = 0;
        int pWidth = mDirector.getViewSize().width();
        int pHeight = mDirector.getViewSize().height();
        if (mParent != null) {
            pLeft = mParent.getPosition().x;
            pTop = mParent.getPosition().y;
        }

        if (mAlign != null) {
            if (mParent != null) {
                pWidth = mParent.getWidth();
                pHeight = mParent.getHeight();
            }

            switch (mAlign) {
                case TOP_LEFT: {
                    mPosition.set(pLeft, pTop);
                    break;
                }
                case TOP_CENTER: {
                    mPosition.set(pLeft + (pWidth - getWidth()) / 2, pTop + 0);
                    break;
                }
                case TOP_RIGHT: {
                    mPosition.set(pLeft + pWidth - getWidth(), pTop + 0);
                    break;
                }
                case CENTER_LEFT: {
                    mPosition.set(pLeft, pTop + (pHeight - getHeight()) / 2);
                    break;
                }
                case CENTER_CENTER: {
                    mPosition.set(pLeft + (pWidth - getWidth()) / 2, pTop + (pHeight - getHeight()) / 2);
                    break;
                }
                case CENTER_RIGHT: {
                    mPosition.set(pLeft + pWidth - getWidth(), pTop + (pHeight - getHeight()) / 2);
                    break;
                }
                case BOTTOM_LEFT: {
                    mPosition.set(pLeft + 0, pTop + pHeight - getHeight());
                    break;
                }
                case BOTTOM_CENTER: {
                    mPosition.set(pLeft + (pWidth - getWidth()) / 2, pTop + pHeight - getHeight());
                    break;
                }
                case BOTTOM_RIGHT: {
                    mPosition.set(pLeft + pWidth - getWidth(), pTop + pHeight - getHeight());
                    break;
                }
                default: {
                    mPosition.set(pLeft + mX, pTop + mY);
                    break;
                }
            }
        } else {
            mPosition.set(pLeft + mX, pTop + mY);
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    public void setColor(int color) {
        if (mPaint == null)
            mPaint = new Paint();
        mPaint.setColor(color);
    }

    /**
     * 检查所有节点是否有变化
     *
     * @return
     */
    public boolean isActive() {
        return true;
    }

    /**
     * 窗口大小变化
     *
     * @param view
     * @param rect
     */
    public void onSizeChange(RenderView view, Rect rect) {
    }

    /**
     * 是否合法
     *
     * @return 是否需要绘制
     */
    public boolean isValid() {
        return true;
    }

    public Director getDirector() {
        return mDirector;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getId() {
        return mId;
    }

    public CNode findNodeById(String id) {
        if (id != null && id.equals(getId())) {
            return this;
        }
        return null;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag){
        this.mTag = tag;
    }

    protected void onTouchDown() {
    }

    protected void onTouchUp() {
    }

    private OnNodeClickListener mClickListener;

    public void setOnNodeClickListener(OnNodeClickListener listener) {
        this.mClickListener = listener;
    }

    public static interface OnNodeClickListener {
        void onClick(CNode node);
    }

    private boolean mTouchable = false;

    public void setTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }

    public boolean isTouchable() {
        return mTouchable;
    }
}
