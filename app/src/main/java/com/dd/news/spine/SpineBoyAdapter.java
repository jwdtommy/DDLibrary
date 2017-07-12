package com.dd.news.spine;

import com.adong.spineaminationlibrary.SpineBaseAdapter;
import com.badlogic.gdx.Files;

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

    }

    @Override
    public void doClick() {

    }
}
