package com.hyena.framework.samples.animator.parser.node;

import com.hyena.framework.samples.animator.parser.action.MapAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapNode {

    private String mId;
    private int mWidth, mHeight;
    private int mX, mY;
    private int mZIndex;
    private String mTag;
    private float mAnchorX = 0.0f, mAnchorY = 0.0f;

    public List<MapAction> mActions;

    public MapNode(String id, int width, int height) {
        this.mId = id;
        this.mWidth = width;
        this.mHeight = height;
    }

    public void addAction(MapAction action) {
        if (mActions == null) {
            mActions = new ArrayList<MapAction>();
        }
        mActions.add(action);
    }

    public List<MapAction> getActions() {
        return mActions;
    }


    public void setZIndex(int zIndex) {
        this.mZIndex = zIndex;
    }

    public int getZIndex() {
        return mZIndex;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public String getId() {
        return mId;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setX(int x) {
        this.mX = x;
    }

    public int getX() {
        return mX;
    }

    public void setY(int y) {
        this.mY = y;
    }

    public int getY() {
        return mY;
    }

    public float getAnchorX() {
        return mAnchorX;
    }

    public void setAnchorX(float anchorX) {
        this.mAnchorX = anchorX;
    }

    public float getAnchorY() {
        return mAnchorY;
    }

    public void setAnchorY(float anchorY) {
        this.mAnchorY = anchorY;
    }
}
