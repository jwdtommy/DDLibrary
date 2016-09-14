package com.dd.fakefans.business.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import com.dd.fakefans.business.News.NewsFragment;
import com.dd.fakefans.business.girls.GirlsFragment;
import com.dd.fakefans.entry.Tabs;
import com.dd.fakefans.base.BaseFragment;

/**
 * 首页的ViewPager
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private HashMap<Integer, BaseFragment> fragments = new HashMap<Integer, BaseFragment>();
    private ArrayList<Tabs> tabs = new ArrayList<>();


    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        init();
        this.fm = fm;
    }

    private void init() {
        tabs.add(new Tabs("图片", "10"));
        tabs.add(new Tabs("段子", "29"));
        tabs.add(new Tabs("声音", "31"));
        tabs.add(new Tabs("视频", "41"));
        tabs.add(new Tabs("美图", "4002"));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).title;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = getFragment(tabs.get(position), position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }

    public void notifyData() {
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Object obj = super.instantiateItem(container, position);
        return obj;
    }

    public BaseFragment getFragment(Tabs tab, int position) {
        if (position == 4) {
            GirlsFragment girlsFragment = new GirlsFragment();
            Bundle bundle = addParams(tab, position);
            girlsFragment.setArguments(bundle);
            return girlsFragment;
        } else {
            NewsFragment newsFragment = new NewsFragment();
            Bundle bundle = addParams(tab, position);
            newsFragment.setArguments(bundle);
            return newsFragment;

        }
    }

    private Bundle addParams(Tabs tab, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewsFragment.KEY_TAB, tab);
        return bundle;
    }

    public HashMap<Integer, BaseFragment> getFragments() {
        return fragments;
    }
}
