package fakefans.dd.com.fakefans.net;

import java.util.List;

import fakefans.dd.com.fakefans.entry.NewsData;
import fakefans.dd.com.fakefans.entry.TopChannel;
import fakefans.dd.com.fakefans.entry.base.Result;
import fakefans.dd.com.fakefans.entry.base.ShowApiResult;
import fakefans.dd.com.fakefans.entry.result.NewsGroupResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by adong on 16/8/21.
 */
public interface APIService {
    //  @POST("menu/gettaglist")
    @Headers("Cache-Control: public, max-age=60")
    @GET("menu/gettaglist")
    Observable<Result<List<TopChannel>>> getTopChannels();

    //@POST("content/getcontentlist")
    @GET("content/getcontentlist?categorytype=olympic&systype=cms&timestamp=1470901314&maxid=&sinceid=&adcode=110108&isoCC=cn&city=å\u008C\u0097äº¬å¸\u0082&device_product=Xiaomi&province=å\u008C\u0097äº¬å¸\u0082&userid=6497462&network_state=wifi&MNC=00&client_ver=5.3.2&client_code=73&udid=867389023462830&MCC=460&visit_id=1470901335384&device_os=6.0.1&longitude=116.336748&sp=ä¸\u00ADå\u009B½ç§»å\u008A¨&visit_start_time=1470901335384&ctime=1470901335381&sessionId=b8cb98182fd54c25a9d34c041229b7adwtmKUhdx&platform=android&app_key=2_2015_03_52&device_size=1080.0x1920.0&district=æµ·æ·\u0080å\u008Cº&latitude=40.029338&device_model=Xiaomi-MINOTELTE&securitykey=36540338832c4fb44572ae078c9ddc49&channel_num=xiaomi&citycode=010")
    Call<NewsGroupResult> getNewsGroups(@Query("categoryid") String id);

    @GET("/255-1")//
    Observable<ShowApiResult<NewsData>> getNews(@Query("type") String type, @Query("title") String title, @Query("page") String page);
}
