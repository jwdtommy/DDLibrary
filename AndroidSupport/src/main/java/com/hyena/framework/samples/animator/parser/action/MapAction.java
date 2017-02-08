package com.hyena.framework.samples.animator.parser.action;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapAction {

    private int mRepeat;
    private int mDuration;

    public MapAction(int duration, int repeat) {
        this.mDuration = duration;
        this.mRepeat = repeat;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getRepeat() {
        return mRepeat;
    }

}
