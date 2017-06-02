package com.dd.news.widgets.drag;

/**
 * Created by J.Tommy on 17/4/17.
 */

public interface Draggable {
    void onCaptrue();

    void onMove(float x, float y);

    void onRelease();

}
