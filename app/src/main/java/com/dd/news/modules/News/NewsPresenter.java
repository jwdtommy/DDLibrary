package com.dd.news.modules.News;

import android.content.Context;
import com.dd.news.Subscriber.SubscriberOnNextListener;
import com.dd.news.entry.BuDeJieInfo;
/**
 * Created by adong on 16/8/22.
 */
public class NewsPresenter {

    public NewsPresenter() {

    }

    public void getNews(Context context, SubscriberOnNextListener<BuDeJieInfo> listener, String type, String page) {
     //   HttpHelper.getInstance().getNews(new ProgressSubscriber(listener, context), type, page);
    }
}
