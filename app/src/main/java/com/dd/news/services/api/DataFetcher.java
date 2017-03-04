package com.dd.news.services.api;

import com.dd.framework.services.network.CustomSubscriberListener;
import com.dd.framework.widgets.CustomDataFetcher;
import com.dd.news.App;
import com.dd.news.entry.MeituInfo;
import rx.Observable;
import rx.Subscriber;
/**
 * Created by adong on 16/4/19.
 */
public class DataFetcher extends CustomDataFetcher {

	public DataFetcher(CustomSubscriberListener<?> listener1,CustomSubscriberListener<?> listener2) {
		super(listener1,listener2);
	}

	public void getNews(String type, String page) {
		Observable observable = App.getInstance().getService(APIServiceImpl.class).getAPIBox().getNews(type, "", page);
		App.getInstance().getService(APIServiceImpl.class).execute(getCustomSubscriber(),observable);
	}

	public void getMessages(String channelId, String page) {
		Observable observable = App.getInstance().getService(APIServiceImpl.class).getAPIBox().getMessages(channelId, page);
		App.getInstance().getService(APIServiceImpl.class).execute(getCustomSubscriber(), observable);
	}

	public void getGirls(Subscriber<MeituInfo> subscriber, String type, String page) {
//		Observable observable = apiService.getGirls(type, page)
//				.map(new HttpResultFunc<MeituInfo>());
//		toSubscribe(observable, subscriber);
	}
}
