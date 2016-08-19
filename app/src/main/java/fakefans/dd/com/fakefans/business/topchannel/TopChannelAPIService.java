package fakefans.dd.com.fakefans.business.topchannel;
import fakefans.dd.com.fakefans.entry.result.TopChannelResult;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by adong on 16/4/19.
 */
public interface TopChannelAPIService {
  //  @POST("menu/gettaglist")
    @POST("menu/gettaglist")
   Call<TopChannelResult> getTopChannels();
}
