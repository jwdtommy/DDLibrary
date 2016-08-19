package fakefans.dd.com.fakefans.business.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import fakefans.dd.com.fakefans.R;
import fakefans.dd.com.fakefans.business.topchannel.TopChannelEvent;
import fakefans.dd.com.fakefans.business.topchannel.TopChannelPresenter;
import fakefans.dd.com.fakefans.data.DataManager;
import fakefans.dd.com.fakefans.ui.base.BaseActivity;

public class HomeActivity extends BaseActivity {

     @Bind(R.id.tab_FindFragment_title)TabLayout tabLayout;
     @Bind(R.id.vp_FindFragment_pager) ViewPager viewPager;
    private HomePagerAdapter homePagerAdapter;
    @Override
    public int onCreateView() {
        return R.layout.activity_home;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        TopChannelPresenter topChannelPresenter=new TopChannelPresenter();
        topChannelPresenter.getTopChannels();
    }

    private void init()
    {
        homePagerAdapter=new HomePagerAdapter(this.getSupportFragmentManager());
    }

    @Subscribe
    public void onEvent(TopChannelEvent topChannelEvent) {
        //设置TabLayout的模式
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        homePagerAdapter.notify(DataManager.topChannels);
        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
    };

}
