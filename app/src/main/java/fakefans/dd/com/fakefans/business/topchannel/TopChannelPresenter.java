package fakefans.dd.com.fakefans.business.topchannel;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import fakefans.dd.com.fakefans.Subscriber.ProgressSubscriber;
import fakefans.dd.com.fakefans.Subscriber.SubscriberOnNextListener;
import fakefans.dd.com.fakefans.data.DataManager;
import fakefans.dd.com.fakefans.entry.TopChannel;
import fakefans.dd.com.fakefans.entry.result.TopChannelResult;
import fakefans.dd.com.fakefans.net.HttpHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by adong on 16/4/19.
 */
public class TopChannelPresenter {

    public TopChannelPresenter()
    {

    }

    public void getTopChannel(SubscriberOnNextListener<List<TopChannel>> listener, Context context)
    {
        HttpHelper.getInstance().getTopchannel(new ProgressSubscriber(listener,context));
    }

}
