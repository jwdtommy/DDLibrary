//package fakefans.dd.com.fakefans.business.home;
//
//import android.util.Log;
//import fakefans.dd.com.fakefans.entry.TopChannel;
//import fakefans.dd.com.fakefans.entry.result.NewsGroupResult;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * Created by adong on 16/4/20.
// */
//public class HomePresenter {
//
//    public void getNewsGroup(TopChannel topChannel)
//    {
//        Retrofit retrofit = new Retrofit.Builder()
//               .baseUrl("http://rmrbapi.people.cn/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        HomeAPIService service=retrofit.create(HomeAPIService.class);
//
//        Call<NewsGroupResult> call=service.getNewsGroups(topChannel.getCategory_id());
//        call.enqueue(new Callback<NewsGroupResult>() {
//            @Override
//            public void onResponse(Call<NewsGroupResult> call, Response<NewsGroupResult> response) {
//                Log.i("jwd","NewsGroupResult="+response.body());
//            }
//
//            @Override
//            public void onFailure(Call<NewsGroupResult> call, Throwable t) {
//
//            }
//        });
//    }
//}
