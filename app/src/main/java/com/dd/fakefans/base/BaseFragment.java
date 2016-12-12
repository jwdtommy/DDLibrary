package com.dd.fakefans.base;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.fakefans.R;

import butterknife.ButterKnife;

/**
 * Created by adong on 16/4/20.
 */
public abstract class BaseFragment<T extends  FragmentHelper> extends SafeFragment {
    private NavigateController mNavigateController;
    private BaseFragment mParentFragment;
    @Nullable
    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        ButterKnife.bind(this,getView());
    }

    public void setNavigateController(NavigateController navigateController) {
        mNavigateController = navigateController;
    }

    public static <T2 extends BaseFragment<?>> T2 newFragment(Activity activity,Class cls, Bundle bundle) {
        if(activity==null){
            return null;
        }
        T2 fragment = (T2)Fragment.instantiate(activity, cls.getName(), bundle);
        return fragment;
    }
    /**
     * 显示子场景
     * @param fragment
     */
    public void showFragment(BaseFragment fragment) {
        checkNavigateController();
        if (mNavigateController != null) {
            fragment.setArguments(mNavigateController, this);
            mNavigateController.addSubFragment(fragment);
        }
    }
    /**
     * 设置主场景
     * @param negigativeController
     * @return
     */
    private BaseFragment setArguments(
            NavigateController negigativeController,
            BaseFragment parentFragment) {
        this.mNavigateController = negigativeController;
        this.mParentFragment = parentFragment;
        return this;
    }

    /**
     * 检查导航控制器是否合法
     */
    protected void checkNavigateController(){
        if (mNavigateController == null) {
            if (getActivity() == null) {
                return;
            }
            if (getActivity() instanceof NavigateController) {
                mNavigateController = (NavigateController)getActivity();
            }
        }
    }
    /**
     * 按键点击
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        return false;
    }
    /**
     * 处理点击事件
     * @param keyCode
     * @param event
     * @return
     */
    public final boolean handleKeyDown(int keyCode, KeyEvent event){
        try {
            return onKeyDown(keyCode, event);
        } catch (Throwable e) {
        }
        return true;
    }
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
    /**
     * 当前Fragment是否可见
     * @param visible
     */
    public void setVisibleToUser(boolean visible) {
    }
    /**
     * 窗口大小变化
     */
    public void onWindowVisibleSizeChange(Rect rect) {}
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        ButterKnife.unbind(this);
    }
}
