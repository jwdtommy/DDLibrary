package com.hyena.framework.samples.animator.parser.action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/22.
 */
public class MapActionSequence extends MapAction {

    private List<MapAction> mActions;

    public MapActionSequence(int duration, int repeat) {
        super(duration, repeat);
    }

    public void addAction(MapAction action) {
        if (mActions == null)
            mActions = new ArrayList<MapAction>();
        mActions.add(action);
    }

    public List<MapAction> getActions() {
        return mActions;
    }
}
