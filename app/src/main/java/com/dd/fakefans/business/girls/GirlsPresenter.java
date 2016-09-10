package com.dd.fakefans.business.girls;

import android.content.Context;

import com.dd.fakefans.Subscriber.ProgressSubscriber;
import com.dd.fakefans.Subscriber.SubscriberOnNextListener;
import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.entry.base.MeituInfo;
import com.dd.fakefans.net.HttpHelper;

/**
 * Created by adong on 16/8/22.
 */
public class GirlsPresenter {

    public GirlsPresenter() {

    }

    public void getGirls(Context context, SubscriberOnNextListener<MeituInfo> listener, String type, String page) {
        HttpHelper.getInstance().getGirls(new ProgressSubscriber(listener, context), type, page);
    }

}
