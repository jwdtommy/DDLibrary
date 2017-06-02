package com.dd.news.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import com.dd.framework.base.CustomFragment;
import com.dd.news.R;

/**
 * Created by J.Tommy on 17/3/9.
 */

public class SceneFragment extends CustomFragment {
    ViewGroup mSceneRoot;
    Scene mScene1;
    Scene mScene2;

    @Override
    public View onCreateCenterViewImpl(@Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.test_layout_scene, null);
    }

    @Nullable
    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mSceneRoot = (ViewGroup) getRootView().findViewById(R.id.fl_secen_root);
        mScene1 = Scene.getSceneForLayout(mSceneRoot, R.layout.scene1, getActivity());
        mScene2 = Scene.getSceneForLayout(mSceneRoot, R.layout.scene2, getActivity());
        final Transition mFadeTransitionOut =
                TransitionInflater.from(getActivity()).inflateTransition(R.transition.fade_transition_out);
        final Transition mFadeTransitionIn =
                TransitionInflater.from(getActivity()).inflateTransition(R.transition.fade_transition_in);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                TransitionManager.go(mScene1);
                TransitionManager.go(mScene2);
            }
        }, 2000);
    }

    @Override
    public void onProcessImpl(int action) {
        super.onProcessImpl(action);
    }
}
