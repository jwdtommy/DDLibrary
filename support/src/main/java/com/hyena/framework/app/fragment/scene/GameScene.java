package com.hyena.framework.app.fragment.scene;

import android.view.MotionEvent;
import android.view.View;

import com.hyena.framework.animation.CScene;
import com.hyena.framework.animation.Director;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.GameFragment;

/**
 * Created by yangzc on 16/4/18.
 */
public class GameScene extends CScene {

    private GameFragment<? extends BaseUIFragmentHelper> mGameFragment;

    public GameScene(GameFragment<? extends BaseUIFragmentHelper> gameFragment
            , Director director) {
        super(director);
        this.mGameFragment = gameFragment;
    }

    public BaseUIFragment getBaseUIFragment() {
        return mGameFragment;
    }

    @Override
    public void onSceneStart() {
        super.onSceneStart();
    }

    @Override
    public void onSceneResume() {
        super.onSceneResume();
    }

    @Override
    public void onScenePause() {
        super.onScenePause();
    }

    @Override
    public void onSceneStop() {
        super.onSceneStop();
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return super.onTouch(event);
    }
}
