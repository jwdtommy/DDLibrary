package fakefans.dd.com.fakefans.entry.result;

import java.util.List;

import fakefans.dd.com.fakefans.entry.TopChannel;
import fakefans.dd.com.fakefans.entry.base.BaseResult;

/**
 * Created by adong on 16/4/19.
 */
public class TopChannelResult extends BaseResult {
    List<TopChannel> data;

    public List<TopChannel> getData() {
        return data;
    }

    public void setData(List<TopChannel> data) {
        this.data = data;
    }
}
