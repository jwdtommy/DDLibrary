/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.app.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SimpleStatePagerAdaper<T extends Fragment> extends FragmentStatePagerAdapter {

	private List<T> mItems;

	public SimpleStatePagerAdaper(FragmentManager fm) {
		super(fm);
	}

	public void setItems(List<T> items) {
		this.mItems = items;
	}

	@Override
	public T getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public int getCount() {
		if (mItems == null)
			return 0;
		return mItems.size();
	}

}
