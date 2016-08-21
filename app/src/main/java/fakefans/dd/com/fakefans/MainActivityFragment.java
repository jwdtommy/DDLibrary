package fakefans.dd.com.fakefans;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fakefans.dd.com.fakefans.business.topchannel.TopChannelPresenter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TopChannelPresenter presenter=new TopChannelPresenter();
    //    presenter.getTopChannels();
        return inflater.inflate(R.layout.fragment_home_list, container, false);
    }
}
