package com.dd.news.modules.message;

import android.content.Context;
import com.dd.news.Subscriber.SubscriberOnNextListener;
import com.dd.news.entry.MessageInfo;
/**
 * Created by J.Tommy on 17/2/7.
 */

public class MessagePresenter{
	public void getMessages(Context context, SubscriberOnNextListener<MessageInfo> listener, String channelId, String page) {
		//	HttpHelper.getInstance().getMessages(new CustomSubscriber(listener, context), channelId, page);
	}
}
