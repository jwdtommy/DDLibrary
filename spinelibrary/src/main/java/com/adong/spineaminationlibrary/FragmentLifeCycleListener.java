package com.adong.spineaminationlibrary;

/**
 * Created by J.Tommy on 17/6/5.
 * 自定义底层的fragment跳转不会触发onPause，导致SpineFragment在跳转的时候也不会执行onPause，Spine资源的释放都在onPause中执行，所以需要此接口来模拟onPause的回调时机。
 */

public interface FragmentLifeCycleListener {
    void onVisibleChange(boolean isVisible);
}
