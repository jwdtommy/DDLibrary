package com.hyena.framework.app.fragment;

import android.os.Bundle;
import android.view.View;

import com.hyena.framework.animation.CScene;
import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.RenderView;

/**
 * Created by yangzc on 16/4/18.
 */
public class GameFragment<T extends BaseUIFragmentHelper> extends
        BaseUIFragment<T> {

    private Director mDirector;

//    public RenderView buildRenderView() {
//        return new CGLView(getActivity());
//    }

    public Director getDirector() {
        return mDirector;
    }

    public void showScene(CScene scene) {
        if (mDirector != null) {
            mDirector.showScene(scene);
        }
    }

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        mDirector = new Director(getActivity());
    }

//    @Override
//    public View onCreateViewImpl(Bundle savedInstanceState) {
//        RenderView renderView = buildRenderView();
//        if (mDirector != null) {
//            mDirector.setRenderView(renderView);
//        }
//        return (View) renderView;
//    }

    public void setRenderView(RenderView renderView){
        if (mDirector != null) {
            mDirector.setRenderView(renderView);
        }
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        if (mDirector != null) {
            mDirector.start();
        }
    }

    @Override
    public void onResumeImpl() {
        super.onResumeImpl();
        if (mDirector != null) {
            mDirector.resumeScene();
        }
    }

    @Override
    public void onPauseImpl() {
        super.onPauseImpl();
        if (mDirector != null) {
            mDirector.pauseScene();
        }
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        if (mDirector != null) {
            mDirector.stop();
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mDirector != null) {
            mDirector.release();
        }
    }
}
