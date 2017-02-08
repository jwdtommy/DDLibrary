package com.hyena.framework.samples.animator.scene;

import android.text.TextUtils;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.samples.animator.render.MapScene;
import com.hyena.framework.samples.animator.render.node.StateSprite;

/**
 * Created by yangzc on 16/4/19.
 */
public class SampleScene extends MapScene {

    private static final String TAG = "SampleScene";
    private GameFragment<? extends BaseUIFragmentHelper> mGameFragment;

    public SampleScene(GameFragment<? extends BaseUIFragmentHelper> gameFragment
            , Director director) {
        super(director);
        this.mGameFragment = gameFragment;
    }

    @Override
    public void load(String xml, int screenWidth, int screenHeight) {
        super.load(xml, screenWidth, screenHeight);
        //初始化所有关卡
        setLevelStatus("level_1", STATUS_LEVEL_OPEN);
        setLevelStatus("level_2", STATUS_LEVEL_UNLOCK);
        setLevelStatus("level_3", STATUS_LEVEL_UNLOCK);
        setLevelStatus("level_4", STATUS_LEVEL_UNLOCK);
        setLevelStatus("level_5", STATUS_LEVEL_UNLOCK);
        setLevelStatus("level_6", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_7", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_8", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_9", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_10", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_11", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_12", STATUS_LEVEL_LOCKED);
        setLevelStatus("level_13", STATUS_LEVEL_LOCKED);

        //宝箱状态
        setBoxStatus("bag_2_3", STATUS_BAG_OPENED);
        setBoxStatus("bag_4_5", STATUS_BAG_OPENED);
        setBoxStatus("bag_6_7", STATUS_BAG_ENABLE);
        setBoxStatus("bag_9_10", STATUS_BAG_UNABLE);
        setBoxStatus("bag_11_12", STATUS_BAG_UNABLE);
        setAnchor("level_1");
    }

    public BaseUIFragment getBaseUIFragment() {
        return mGameFragment;
    }

    private OnNodeClickListener mNodeClickListener = new OnNodeClickListener() {

        @Override
        public void onClick(CNode node) {
//            LogUtil.v(TAG, "onNodeClick: " + node.getId());
            String id = node.getId();
            if (!TextUtils.isEmpty(id) && id.startsWith("level")) {
                setAnchor(node.getId(), true);
            }
        }
    };

    @Override
    public void setLevelStatus(String levelId, int status) {
        super.setLevelStatus(levelId, status);
        try {
            StateSprite levelSprite = (StateSprite) findNodeById(levelId);
            if (levelSprite != null) {
                switch (status) {
                    case STATUS_LEVEL_LOCKED: {
                        levelSprite.setOnNodeClickListener(null);
                        break;
                    }
                    case STATUS_LEVEL_UNLOCK: {
                        levelSprite.setOnNodeClickListener(mNodeClickListener);
                        break;
                    }
                    case STATUS_LEVEL_OPEN: {
                        levelSprite.setOnNodeClickListener(mNodeClickListener);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
