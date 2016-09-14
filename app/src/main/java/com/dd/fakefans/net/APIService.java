package com.dd.fakefans.net;

import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.entry.base.MeituInfo;
import com.dd.fakefans.entry.base.ShowApiResult;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;
/**
 * Created by adong on 16/8/21.
 */
public interface APIService {
    @Headers("Cache-Control: public, max-age=60")
    @GET("/255-1")//
    Observable<ShowApiResult<BuDeJieInfo>> getNews(@Query("type") String type, @Query("title") String title, @Query("page") String page);

    @Headers("Cache-Control: public, max-age=60")
    @GET("/852-2")//
    Observable<ShowApiResult<MeituInfo>> getGirls(@Query("type") String type, @Query("page") String page);

}
