package com.dd.fakefans.modules.News;

import android.content.Context;

import com.dd.fakefans.Subscriber.ProgressSubscriber.ProgressSubscriber;
import com.dd.fakefans.Subscriber.SubscriberOnNextListener;
import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.net.HttpHelper;

/**
 * Created by adong on 16/8/22.
 */
public class NewsPresenter {

    public NewsPresenter() {

    }

    public void getNews(Context context, SubscriberOnNextListener<BuDeJieInfo> listener, String type, String page) {
        HttpHelper.getInstance().getNews(new ProgressSubscriber(listener, context), type, page);
    }
}
