package com.adong.spinedemo;

import com.adong.spineaminationlibrary.SpineBaseAdapter;
import com.badlogic.gdx.Files;

/**
 * Created by J.Tommy on 17/6/30.
 */

public class SpineGoblinsAdapter extends SpineBaseAdapter {
    @Override
    public void onCreateImpl() {
        setAltasPath("goblins/goblins-pma.atlas", Files.FileType.Internal);
        setSkeletonPath("goblins/goblins.json", Files.FileType.Internal);
    }

    @Override
    public void onCreatedImpl() {
        mAnimationState.setAnimation(0, "walk", true);
    }

    @Override
    public void doClick() {

    }
}
