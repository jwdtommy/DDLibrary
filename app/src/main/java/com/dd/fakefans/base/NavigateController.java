/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.dd.fakefans.base;

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
}
