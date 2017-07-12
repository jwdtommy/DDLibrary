package com.adong.spinedemo;

import com.adong.spineaminationlibrary.SpineBaseAdapter;
import com.badlogic.gdx.Files;
import com.esotericsoftware.spine.Animation;

/**
 * Created by J.Tommy on 17/6/30.
 */

public class SpineBoyAdapter extends SpineBaseAdapter {
    @Override
    public void onCreateImpl() {
        setAltasPath("spineboy/spineboy-pma.atlas", Files.FileType.Internal);
        setSkeletonPath("spineboy/spineboy.json", Files.FileType.Internal);
    }

    @Override
    public void onCreatedImpl() {
        mAnimationState.setAnimation(0, "walk", true);
    }

    @Override
    public void doClick() {
        Animation animation = mSkeletonData.findAnimation("shoot");
        mAnimationState.setAnimation(0, animation, false);
        mAnimationState.addAnimation(0, "walk", true, animation.getDuration());
    }

    public void doJump() {
        Animation animation = mSkeletonData.findAnimation("jump");
        mAnimationState.setAnimation(0, animation, false);
        mAnimationState.addAnimation(0, "walk", true, animation.getDuration());
    }

    public void doRun() {
        Animation animation = mSkeletonData.findAnimation("run");
        mAnimationState.setAnimation(0, animation, false);
        mAnimationState.addAnimation(0, "walk", true, animation.getDuration());
    }
}
