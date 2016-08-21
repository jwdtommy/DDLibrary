package fakefans.dd.com.fakefans.net;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fakefans.dd.com.fakefans.business.topchannel.TopChannelEvent;
import fakefans.dd.com.fakefans.data.DataManager;
import fakefans.dd.com.fakefans.entry.TopChannel;
import fakefans.dd.com.fakefans.entry.base.Result;
import fakefans.dd.com.fakefans.entry.result.TopChannelResult;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by adong on 16/4/19.
 */
public class HttpHelper {
    public static final String BASE_URL = "http://rmrbapi.people.cn/";

    private static final int DEFAULT_TIMEOUT = 5;
    private APIService apiService;

    private Retrofit retrofit;

    private static final class Singlton {
        public static final HttpHelper instatnce = new HttpHelper();
    }

    public static HttpHelper getInstance() {
        return Singlton.instatnce;
    }

    private HttpHelper() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        apiService = retrofit.create(APIService.class);
    }


    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public void getTopchannel(Subscriber<List<TopChannel>> subscriber) {
        Observable observable = apiService.getTopChannels()
                .map(new HttpResultFunc<List<TopChannel>>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<Result<T>, T> {

        @Override
        public T call(Result<T> httpResult) {
            if (httpResult.getErrorCode()!=0) {
                throw new ApiException(httpResult.getErrorCode());//error
            }
            return httpResult.getData();
        }
    }


}
