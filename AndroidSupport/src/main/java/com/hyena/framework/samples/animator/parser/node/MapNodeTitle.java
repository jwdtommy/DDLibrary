package com.hyena.framework.samples.animator.parser.node;

/**
 * Created by yangzc on 16/4/27.
 */
public class MapNodeTitle extends MapNode {


//    <title id="title" x="func(WIDTH/2-61)" y="10" width="123" height="43" background="res:map1/title_bg.png"
//    title="海底世界" titleFontSize="16" titleColor="#ffffff"
//    subTitleLeft="10/" subTitleFontSize="12" subTitleLeftColor="#ffffff"
//    subTitleRight="50" subTitleRightColor="#ffffff"/>

    public String mBackGround = "";
    public String mTitle;
    public int mTitleFontSize;
    public String mTitleColor;

    public int mSubTitleFontSize;

    public String mSubTitleLeft;
    public String mSubTitleLeftColor;
    public String mSubTitleRight;
    public String mSubTitleRightColor;


    public MapNodeTitle(String id, int width, int height) {
        super(id, width, height);
    }
}
