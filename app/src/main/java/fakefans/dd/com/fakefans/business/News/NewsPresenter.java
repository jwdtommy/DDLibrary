package fakefans.dd.com.fakefans.business.News;

import android.content.Context;
import fakefans.dd.com.fakefans.Subscriber.ProgressSubscriber;
import fakefans.dd.com.fakefans.Subscriber.SubscriberOnNextListener;
import fakefans.dd.com.fakefans.entry.NewsData;
import fakefans.dd.com.fakefans.net.HttpHelper;

/**
 * Created by adong on 16/8/22.
 */
public class NewsPresenter {

    public NewsPresenter() {

    }

    public void getNews(Context context, SubscriberOnNextListener<NewsData> listener, String type, String page) {
        HttpHelper.getInstance().getNews(new ProgressSubscriber(listener, context), type, page);
    }
}
