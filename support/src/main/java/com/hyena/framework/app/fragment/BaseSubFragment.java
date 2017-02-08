/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.app.fragment;

import com.hyena.framework.app.NavigateController;

import android.view.View;

/**
 * Fragment基类
 * @author yangzc
 */
public class BaseSubFragment extends HSlidingBackFragment {

	//场景控制器
	private NavigateController mNavigateController;
	//父Fragment
	private BaseSubFragment mParentFragment;

	/**
	 * 设置主场景
	 * 
	 * @param negigativeController
	 * @return
	 */
	public BaseSubFragment setArguments(
			NavigateController negigativeController,
			BaseSubFragment parentFragment) {
		this.mNavigateController = negigativeController;
		this.mParentFragment = parentFragment;
		return this;
	}

	/**
	 * 获得父Fragment
	 * 
	 * @return
	 */
	public BaseSubFragment getParent() {
		return mParentFragment;
	}

	@Override
	public void onPanelSlide(View pPanel, float pSlideOffset) {
		super.onPanelSlide(pPanel, pSlideOffset);
	}

	/**
	 * 获得导航控制器
	 * 
	 * @return
	 */
	public NavigateController getNavigateController() {
        checkNavigateController();
		return mNavigateController;
	}

	/**
	 * 显示子场景
	 * 
	 * @param fragment
	 */
	public void showFragment(BaseSubFragment fragment) {
        checkNavigateController();
		if (mNavigateController != null) {
			fragment.setArguments(mNavigateController, this);
			mNavigateController.addSubFragment(fragment);
		}
	}

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
    }

    @Override
	public void onPanelClosed(View pPanel) {
    	finishWithOutAnim();
		super.onPanelClosed(pPanel);
	}

	/**
	 * 清空所有Fragment
	 */
	public void removeAllFragment() {
        checkNavigateController();
		if (mNavigateController != null) {
			mNavigateController.removeAllFragment();
		}
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
	 * 无动画退出窗口
	 */
	public void finishWithOutAnim(){
		checkNavigateController();
		if (mNavigateController != null) {
			mNavigateController.removeSubFragment(this);
		}
	}
}
