package com.dd.fakefans.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by adong on 16/4/20.
 */
public abstract class BaseCustomFragment extends Fragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(configView(),null);
        ButterKnife.bind(this,view);
        onShow();
        return view;
    }

    public  abstract @LayoutRes int configView();
    public  abstract  void onShow();

}
