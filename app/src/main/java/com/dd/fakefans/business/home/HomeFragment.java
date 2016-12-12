package com.dd.fakefans.business.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.fakefans.R;
import com.dd.fakefans.base.BaseActivity;
import com.dd.fakefans.base.BaseFragment;
import com.dd.fakefans.business.News.NewsFragment;
import com.dd.fakefans.business.girls.GirlsFragment;
import com.dd.fakefans.entry.Tabs;

import butterknife.Bind;

public class HomeFragment extends BaseFragment {

	@Bind(R.id.tv_name)
	TextView mTvName;
	@Bind(R.id.tab_FindFragment_title)
	TabLayout mTabFindFragmentTitle;
	@Bind(R.id.vp_FindFragment_pager)
	ViewPager mVpFindFragmentPager;
	private HomePagerAdapter homePagerAdapter;

	@Nullable
	@Override
	public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return View.inflate(getActivity(),R.layout.activity_home,null);
	}

	@Override
	public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		init();
	}

	private void init() {
		homePagerAdapter = new HomePagerAdapter(this.getChildFragmentManager());
		mVpFindFragmentPager.setAdapter(homePagerAdapter);
		mTabFindFragmentTitle.setupWithViewPager(mVpFindFragmentPager);
	}
}
