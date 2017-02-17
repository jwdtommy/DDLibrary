package com.dd.news.modules.home;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import com.dd.news.modules.News.NewsFragment;
import com.dd.news.modules.girls.GirlsFragment;
import com.dd.news.entry.Tabs;
import com.dd.news.modules.message.MessageFragment;
import com.dd.news.utils.Consts;
import com.dd.framework.base.BaseFragment;

/**
 * 首页的ViewPager
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private HashMap<Integer, BaseFragment> fragments = new HashMap<Integer, BaseFragment>();
    private ArrayList<Tabs> tabs = new ArrayList<>();


    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        init();
    }

    private void init() {
        tabs.add(new Tabs("国内", "5572a108b3cdc86cf39001cd"));
        tabs.add(new Tabs("国际", "5572a108b3cdc86cf39001ce"));
        tabs.add(new Tabs("互联网", "5572a108b3cdc86cf39001d1"));
        tabs.add(new Tabs("房产", "5572a108b3cdc86cf39001d2"));
        tabs.add(new Tabs("汽车", "5572a108b3cdc86cf39001d3"));
        tabs.add(new Tabs("体育", "5572a108b3cdc86cf39001d4"));
        tabs.add(new Tabs("娱乐", "5572a108b3cdc86cf39001d5"));
        tabs.add(new Tabs("游戏", "5572a108b3cdc86cf39001d6"));
        tabs.add(new Tabs("教育", "5572a108b3cdc86cf39001d7"));
        tabs.add(new Tabs("财经", "5572a109b3cdc86cf39001e0"));
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
        if (position == tabs.size()-1) {
            GirlsFragment girlsFragment = new GirlsFragment();
            Bundle bundle = addParams(tab, position);
            girlsFragment.setArguments(bundle);
            return girlsFragment;
        }
        else if(position>=0&&position<10){
            MessageFragment messageFragment=new MessageFragment();
            Bundle bundle = addParams(tab, position);
            messageFragment.setArguments(bundle);
            return messageFragment;
        }
        else {
            NewsFragment newsFragment = new NewsFragment();
            Bundle bundle = addParams(tab, position);
            newsFragment.setArguments(bundle);
            return newsFragment;

        }
    }

    private Bundle addParams(Tabs tab, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Consts.KEY_TAB, tab);
        return bundle;
    }

    public HashMap<Integer, BaseFragment> getFragments() {
        return fragments;
    }
}
