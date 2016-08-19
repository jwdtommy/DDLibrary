package fakefans.dd.com.fakefans.business.topchannel;

import org.greenrobot.eventbus.EventBus;

import fakefans.dd.com.fakefans.data.DataManager;
import fakefans.dd.com.fakefans.entry.result.TopChannelResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adong on 16/4/19.
 */
public class TopChannelPresenter {

    public void getTopChannels()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rmrbapi.people.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TopChannelAPIService service=retrofit.create(TopChannelAPIService.class);

        Call<TopChannelResult> call=service.getTopChannels();
        call.enqueue(new Callback<TopChannelResult>() {
            @Override
            public void onResponse(Call<TopChannelResult> call, Response<TopChannelResult> response) {
                DataManager.topChannels=response.body().getData();
                EventBus.getDefault().post(new TopChannelEvent());
            }

            @Override
            public void onFailure(Call<TopChannelResult> call, Throwable t) {

            }
        });
    }
}
