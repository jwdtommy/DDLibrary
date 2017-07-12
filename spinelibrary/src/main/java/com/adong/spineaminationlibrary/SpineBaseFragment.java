package com.adong.spineaminationlibrary;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badlogic.gdx.backends.android.AndroidGraphics;
/**
 * Created by J.Tommy on 17/5/25.
 */

public class SpineBaseFragment extends AndroidFragmentApplication implements FragmentLifeCycleListener {
    protected SpineBaseAdapter mAdapter;
    private View mView;
    private boolean mIsVisible = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;
        mView = initializeForView(mAdapter, cfg);
        if (graphics.getView() instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) graphics.getView();
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    public SpineBaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onVisibleChange(boolean isVisible) {
        this.mIsVisible = isVisible;
        if (!isVisible) {
            setGone();
        } else {
            setVisible();
        }
    }

    private void setVisible() {
        if (getGraphics() != null) {
            resume();
            View view = ((AndroidGraphics) getGraphics()).getView();
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setGone() {
        if (getGraphics() != null) {
            pause();
            View view = ((AndroidGraphics) getGraphics()).getView();
            view.setVisibility(View.GONE);
        }
    }

    public void setAdapter(SpineBaseAdapter roleSpineAdapter) {
        mAdapter = roleSpineAdapter;
        mAdapter.setParentFragment(this);
    }
}
