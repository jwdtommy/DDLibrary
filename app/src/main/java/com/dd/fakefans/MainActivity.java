package com.dd.fakefans;

import android.os.Bundle;

import com.dd.fakefans.base.BaseFragment;
import com.dd.fakefans.base.NavigateActivity;
import com.dd.fakefans.business.home.HomeFragment;

public class MainActivity extends NavigateActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addSubFragment(BaseFragment.newFragment(this, HomeFragment.class, null));
	}
}
