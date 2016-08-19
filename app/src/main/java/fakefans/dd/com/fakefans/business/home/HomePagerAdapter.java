package fakefans.dd.com.fakefans.business.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fakefans.dd.com.fakefans.entry.TopChannel;
import fakefans.dd.com.fakefans.ui.base.BaseFragment;

/**
 * 首页的ViewPager
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private List<TopChannel> channels;
    private HashMap<Integer, BaseFragment> fragments = new HashMap<Integer, BaseFragment>();

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        channels = new ArrayList<TopChannel>();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).getName();
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = getFragment(channels.get(position), position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }

    public void notify(List<TopChannel> channels) {
        this.channels = channels;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Object obj = super.instantiateItem(container, position);
        return obj;
    }

    public BaseFragment getFragment(TopChannel item, int position) {
            HomeFragment newsFragment = new HomeFragment();
            Bundle bundle = addParams(item, position);
            newsFragment.setArguments(bundle);
            return newsFragment;
    }

    private Bundle addParams(TopChannel item, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeFragment.KEY_TOP_CHANNEL,item);
        return bundle;
    }
    public HashMap<Integer, BaseFragment> getFragments() {
        return fragments;
    }
}
