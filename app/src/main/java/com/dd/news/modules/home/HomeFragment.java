package com.dd.news.modules.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.adong.spineaminationlibrary.SpineBaseFragment;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.dd.framework.base.CustomFragment;
import com.dd.news.R;
import com.dd.news.spine.SpineBoyAdapter;

import butterknife.Bind;

public class HomeFragment extends CustomFragment implements AndroidFragmentApplication.Callbacks{
    @Bind(R.id.tab_FindFragment_title)
    TabLayout mTabFindFragmentTitle;
    @Bind(R.id.vp_FindFragment_pager)
    ViewPager mVpFindFragmentPager;
    private HomePagerAdapter homePagerAdapter;
    private SpineBaseFragment mSpineBaseFragment;

    @Override
    public View onCreateCenterViewImpl(@Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.activity_home, null);
    }

    @Override
    public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        init();
        mSpineBaseFragment = new SpineBaseFragment();
        mSpineBaseFragment.setAdapter(new SpineBoyAdapter());

        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_spine_container, mSpineBaseFragment);
        transaction.commitAllowingStateLoss();
    }

    private void init() {
        homePagerAdapter = new HomePagerAdapter(this.getChildFragmentManager());
        mVpFindFragmentPager.setAdapter(homePagerAdapter);
        mTabFindFragmentTitle.setupWithViewPager(mVpFindFragmentPager);
    }

    @Override
    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        super.setEnterSharedElementCallback(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getActivity().finish();
        }
        return false;
    }

    @Override
    public void exit() {

    }
}
