package com.hyena.framework.samples.animator.fragment;

import android.os.Bundle;
import android.view.View;

import com.hyena.framework.animation.RenderView;
import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.samples.R;
import com.hyena.framework.samples.animator.scene.SampleScene;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/19.
 */
public class GameSampleFragment extends GameFragment {

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_game_map, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        RenderView renderView = (RenderView) view.findViewById(R.id.rv_game);
        setRenderView(renderView);

        int screenWidthDp = UIUtils.px2dip(UIUtils.getWindowWidth(getActivity()));
        int screenHeightDp = UIUtils.px2dip(UIUtils.getWindowHeight(getActivity()));
        SampleScene scene = new SampleScene(this, getDirector());
        showScene(scene);
        scene.loadAssetPath("mapdemo.xml", screenWidthDp, screenHeightDp);
    }
}
