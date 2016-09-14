package com.dd.fakefans.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;

import butterknife.ButterKnife;

/**
 * Created by adong on 16/4/20.
 */
public abstract class BaseActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onCreateView());
        ButterKnife.bind(this);
     //   EventBus.getDefault().register(this);
    }
    public abstract @LayoutRes int onCreateView();
}
