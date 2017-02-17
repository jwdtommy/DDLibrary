package com.dd.news.modules.girls;

import android.content.Context;

//import com.dd.news.Subscriber.ProgressSubscriber.ProgressSubscriber;
import com.dd.news.Subscriber.SubscriberOnNextListener;
import com.dd.news.entry.MeituInfo;
//import com.dd.news.services.net.HttpHelper;

/**
 * Created by adong on 16/8/22.
 */
public class GirlsPresenter {

    public GirlsPresenter() {

    }

    public void getGirls(Context context, SubscriberOnNextListener<MeituInfo> listener, String type, String page) {
    //    HttpHelper.getInstance().getGirls(new ProgressSubscriber(listener, context), type, page);
    }

}
