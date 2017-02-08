package com.hyena.framework.samples.animator.parser.action;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapActionFrame extends MapAction {

    private List<MapFrame> mMapFrames;

    public MapActionFrame(int duration, int repeat) {
        super(duration, repeat);
    }

    @Override
    public int getDuration() {
        int duration = 0;
        if (mMapFrames != null) {
            for (int i = 0; i < mMapFrames.size(); i++) {
                duration += mMapFrames.get(i).getDuration();
            }
        }
        return duration;
    }

    public void addFrame(MapFrame frame) {
        if (mMapFrames == null)
            mMapFrames = new ArrayList<MapFrame>();
        mMapFrames.add(frame);
    }

    public List<MapFrame> getFrames() {
        return mMapFrames;
    }
}
