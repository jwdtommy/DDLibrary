package com.hyena.framework.samples.animator.parser.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapNodeLayer {

    private String mId;
    private int mZIndex;
    private float mDepth;
    private List<MapNode> mNodes;
    private int mWidth, mHeight;

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setZIndex(int zIndex) {
        this.mZIndex = zIndex;
    }

    public int getZIndex() {
        return mZIndex;
    }

    public void setDepth(float depth) {
        this.mDepth = depth;
    }

    public float getDepth() {
        return mDepth;
    }

    public void addNode(MapNode node) {
        if (mNodes == null)
            mNodes = new ArrayList<MapNode>();
        mNodes.add(node);
//        sort();
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getId() {
        return mId;
    }

    public List<MapNode> getNodes() {
        return mNodes;
    }

//    private void sort() {
//        if (mNodes != null) {
//            Collections.sort(mNodes, new Comparator<MapNode>() {
//                @Override
//                public int compare(MapNode lhs, MapNode rhs) {
//                    return lhs.getZIndex() - rhs.getZIndex();
//                }
//            });
//        }
//    }
}
