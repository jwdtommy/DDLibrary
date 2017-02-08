package com.dd.fakefans.modules.message;

import android.content.Context;

import com.dd.fakefans.Subscriber.ProgressSubscriber.ProgressSubscriber;
import com.dd.fakefans.Subscriber.SubscriberOnNextListener;
import com.dd.fakefans.entry.MessageInfo;
import com.dd.fakefans.net.HttpHelper;

/**
 * Created by J.Tommy on 17/2/7.
 */

public class MessagePresenter{
	public void getMessages(Context context, SubscriberOnNextListener<MessageInfo> listener, String channelId, String page) {
			HttpHelper.getInstance().getMessages(new ProgressSubscriber(listener, context), channelId, page);
	}
}
