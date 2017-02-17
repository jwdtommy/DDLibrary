package com.dd.news;

import android.os.Bundle;

import com.dd.news.modules.home.HomeFragment;
import com.dd.framework.base.BaseFragment;
import com.dd.framework.base.NavigateActivity;

public class MainActivity extends NavigateActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addSubFragment(BaseFragment.newFragment(this, HomeFragment.class, null));
	}
}
