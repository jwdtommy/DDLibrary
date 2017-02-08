package com.hyena.framework.samples.animator.parser.node;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapNodeText extends MapNode {

    public String mText;
    public String mColor;
    public String mPressColor;
    public int mFontSize;
    public String mAlign;

    public String mStyle;

    public MapNodeText(String id, int width, int height) {
        super(id, width, height);
    }

}
