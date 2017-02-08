/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.app.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.hyena.framework.BuildConfig;
import com.hyena.framework.app.NavigateController;
import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ResourceUtils;

import java.util.List;
import java.util.Stack;

/**
 * 导航控制器
 * @author yangzc
 */
@SuppressLint("NewApi")
public abstract class NavigateActivity extends BaseActivity implements NavigateController {
	
	private BaseFragment mCurrentFragment;
	// 后退栈
	private Stack<BaseFragment> mSubFragmentStack;
	private View mSubViewContainer;

	private int mLayoutId;
	private int mMainPanelId;
	private int mSubPagePanelId;

	@Override
	protected void onCreate(Bundle bundle) {
		if (LogUtil.isDebug() && BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 9) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
		}
		super.onCreate(bundle);
		mLayoutId = ResourceUtils.getLayoutId("activity_main");
		mMainPanelId = ResourceUtils.getId("main_container");
		mSubPagePanelId = ResourceUtils.getId("main_subpage_container");
		onPreCreate();
		setContentView(mLayoutId);
		mSubViewContainer = findViewById(mSubPagePanelId);
		registerWindowLayoutListener();
		reInitBackStack();
	}

	public void onPreCreate() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
	}

	private void registerWindowLayoutListener() {
		final View rootView = getWindow().getDecorView();
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect rect = new Rect();
				rootView.getWindowVisibleDisplayFrame(rect);
				if (mSubFragmentStack != null && !mSubFragmentStack.isEmpty()) {
					BaseFragment topFragment = mSubFragmentStack.peek();
					if (topFragment != null)
						topFragment.onWindowVisibleSizeChange(rect);
					return;
				}
				if (mCurrentFragment != null) {
					mCurrentFragment.onWindowVisibleSizeChange(rect);
				}
			}
		});
	}

	/**
	 * 获得当前的Fragment
	 * @return
	 */
	public BaseFragment getCurrentFragment(){
		return mCurrentFragment;
	}

	// =========================主Fragment导航逻辑=========================
	/**
	 * 显示主框架
	 * @param fragment
	 */
	public void showFragment(BaseFragment fragment) {
		if (isFinishing())
			return;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(mMainPanelId, fragment);
		transaction.commitAllowingStateLoss();
		this.mCurrentFragment = fragment;
	}
	
	/**
	 * 清除当前Fragment
	 */
	public void clearMainFragment(){
		if (isFinishing())
			return;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.remove(mCurrentFragment);
		transaction.commitAllowingStateLoss();
	}

	// =========================子Fragment导航逻辑=========================
	/**
	 * 显示子view
	 * @param fragment
	 */
	@Override
	public void addSubFragment(BaseFragment fragment) {
		if (isFinishing())
			return;
		//检查后退栈
		if (mSubFragmentStack != null && mSubFragmentStack.size() > 0
				&& isSameFragment(mSubFragmentStack.peek(), fragment)) {
			return;
		}

		mSubViewContainer.setVisibility(View.VISIBLE);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.add(mSubPagePanelId, fragment);
//		transaction.addToBackStack(null);
        getSupportFragmentManager().popBackStack();
		transaction.commitAllowingStateLoss();
		if (mSubFragmentStack == null) {
			mSubFragmentStack = new Stack<BaseFragment>();
		}
		if (mSubFragmentStack.size() > 0) {
			mSubFragmentStack.peek().setVisibleToUser(false);
		}
		mSubFragmentStack.add(fragment);
		fragment.setVisibleToUser(true);
	}

	/**
	 * 清空Fragment
	 */
	@Override
	public void removeAllFragment() {
		if (isFinishing())
			return;
		
		if(mCurrentFragment != null && mCurrentFragment.isInLayout()){
			mCurrentFragment.setVisibleToUser(false);
		}
		
		if (mSubFragmentStack == null || mSubFragmentStack.isEmpty()) {
			return;
		}
		mSubViewContainer.setVisibility(View.GONE);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		for (BaseFragment fragment : mSubFragmentStack) {
			transaction.remove(fragment);
			fragment.setVisibleToUser(false);
		}
		transaction.commitAllowingStateLoss();
		mSubFragmentStack.clear();
		if (mSubFragmentStack == null || mSubFragmentStack.isEmpty()) {
			mSubViewContainer.setVisibility(View.GONE);
		}
		if(mCurrentFragment != null){
			mCurrentFragment.setVisibleToUser(true);
		}
	}

	/**
	 * 是否是相同的Fragment
	 * 
	 * @param topFragment
	 * @param currentFragment
	 * @return
	 */
	private boolean isSameFragment(BaseFragment topFragment,
			BaseFragment currentFragment) {
		if (topFragment == null || currentFragment == null)
			return false;

		boolean isSameType = ((Object)topFragment).getClass().getName()
				.equals(((Object)currentFragment).getClass().getName());

        if(topFragment.getArguments() != null && currentFragment.getArguments() != null){
            boolean isSameArgument = topFragment.getArguments().toString()
                    .equals(currentFragment.getArguments().toString());
            return isSameType && isSameArgument;
        } else if(topFragment.getArguments() == null && currentFragment.getArguments() == null) {
            return isSameType;
        } else {
            return false;
        }
	}

	/**
	 * 删除子view
	 * 
	 * @param fragment
	 */
	@Override
	public void removeSubFragment(BaseFragment fragment) {
		if (isFinishing())
			return;

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.remove(fragment);
		transaction.commitAllowingStateLoss();
		if (mSubFragmentStack != null && !mSubFragmentStack.isEmpty()) {
			//TODO
			mSubFragmentStack.remove(fragment);
		}
		if (mSubFragmentStack != null && mSubFragmentStack.size() > 0) {
			mSubFragmentStack.peek().setVisibleToUser(true);
		}
		if (mSubFragmentStack == null || mSubFragmentStack.isEmpty()) {
			mSubViewContainer.setVisibility(View.GONE);
			if(mCurrentFragment != null){
				mCurrentFragment.setVisibleToUser(true);
			}
		}
	}
	
	/**
	 * 检查后退栈
	 */
	private void reInitBackStack(){
		FragmentManager manager = getSupportFragmentManager();
		List<Fragment> fragments = manager.getFragments();
		if (fragments != null && !fragments.isEmpty()) {
			if (mSubFragmentStack == null) {
				mSubFragmentStack = new Stack<BaseFragment>();
			}
			mSubFragmentStack.clear();
			for (int i = 0; i < fragments.size(); i++) {
				Fragment fragment = fragments.get(i);
				if (fragment != mCurrentFragment && fragment instanceof BaseFragment) {
					mSubFragmentStack.push((BaseFragment) fragment);
				}
			}
		}
		if (mSubFragmentStack != null && mSubFragmentStack.size() > 0) {
			mSubViewContainer.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
        popSubFragment();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mSubFragmentStack != null && !mSubFragmentStack.isEmpty()) {
			BaseFragment topFragment = mSubFragmentStack.peek();
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (topFragment.handleKeyDown(keyCode, event)) {
					return true;
				}
				removeSubFragment(topFragment);
				return true;
			} else {
				return topFragment.handleKeyDown(keyCode, event);
			}
		}
		if (mCurrentFragment != null) {
			boolean isHandle = mCurrentFragment.handleKeyDown(keyCode, event);
            if(isHandle)
                return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    /**
     * pop last fragment
     */
	private void popSubFragment(){
        if (mSubFragmentStack != null && !mSubFragmentStack.isEmpty()) {
            BaseFragment topFragment = mSubFragmentStack.peek();
            removeSubFragment(topFragment);
        }
    }

	/*
	 * TODO
	 * 兼容处理个别产品个别处理不正常逻辑
	 */
	
	//是否手动处理ActivityResult
	private boolean mHanldeActivityResult = false;
	/**
	 * 是否手动处理ActivityResult
	 */
	public void handleActivityResult(){
		mHanldeActivityResult = true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(!mHanldeActivityResult)
			return;
		
		try {
			if (mSubFragmentStack != null && mSubFragmentStack.size() > 0) {
				mSubFragmentStack.peek().onActivityResult(requestCode, resultCode, data);
			} else {
				if (mCurrentFragment != null) {
					mCurrentFragment.onActivityResult(requestCode, resultCode, data);
				}
			}
		} catch (Exception e) {
			LogUtil.e("NavigateActivity", e);
		}
	}

	@TargetApi(19)
	public void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
}
