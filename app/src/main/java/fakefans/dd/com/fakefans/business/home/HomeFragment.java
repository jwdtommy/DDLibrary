package fakefans.dd.com.fakefans.business.home;

import android.util.Log;

import fakefans.dd.com.fakefans.entry.TopChannel;
import fakefans.dd.com.fakefans.ui.base.BaseListFragment;

/**
 * Created by adong on 16/4/20.
 */
public class HomeFragment extends BaseListFragment {
    private TopChannel topChannel;
    private  HomePresenter homePresenter;
    public  static  final String KEY_TOP_CHANNEL="topchannel";
    @Override
    public void onShow() {
        super.onShow();
        topChannel= (TopChannel) getArguments().getSerializable(KEY_TOP_CHANNEL);

        Log.i("jwd","topChannel ="+topChannel.toString());
        homePresenter =new HomePresenter();
        homePresenter.getNewsGroup(topChannel);
    }
}
