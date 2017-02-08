/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app;

import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;

/**
 * 导航控制
 * @author yangzc
 */
public interface NavigateController {

	/**
	 * 添加子Fragment
	 * @param fragment
	 */
	public void addSubFragment(BaseFragment fragment);

	/**
	 * 移除Fragment
	 * @param fragment
	 */
	public void removeSubFragment(BaseFragment fragment);

	/**
	 * 清空所有的Fragment
	 */
	public void removeAllFragment();

	/**
	 * 获得UI帮助类
	 * @param fragment
	 * @param <T>
	 * @return
	 */
	public <T extends BaseUIFragmentHelper> T getUIFragmentHelper(BaseFragment fragment);
}
