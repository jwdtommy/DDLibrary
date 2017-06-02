package com.dd.news.widgets.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * Created by J.Tommy on 17/4/14.
 */

public class DragItemView extends ImageView implements Draggable {
    private int mSrcLeft;
    private int mSrcTop;
    public DragItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSrcLeft=getLeft();
        mSrcTop=getTop();
    }

    public int getSrcLeft() {
        return mSrcLeft;
    }

    public int getSrcTop() {
        return mSrcTop;
    }

    @Override
    public void onCaptrue() {

    }

    @Override
    public void onMove(float x, float y) {

    }

    @Override
    public void onRelease() {

    }
}
