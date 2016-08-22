package fakefans.dd.com.fakefans.business.home;

import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.byteam.superadapter.SuperAdapter;

import fakefans.dd.com.fakefans.Subscriber.SubscriberOnNextListener;
import fakefans.dd.com.fakefans.business.News.NewsPresenter;
import fakefans.dd.com.fakefans.entry.NewsData;
import fakefans.dd.com.fakefans.entry.Tabs;
import fakefans.dd.com.fakefans.ui.base.BaseListFragment;

/**
 * Created by adong on 16/4/20.
 */
public class HomeFragment extends BaseListFragment implements SubscriberOnNextListener<NewsData> {
    private Tabs tab;

    private NewsPresenter newsPresenter;

    public  static  final String KEY_TAB="key_tab";
    @Override
    public void onShow() {
        super.onShow();
        tab= (Tabs) getArguments().getSerializable(KEY_TAB);
        newsPresenter=new NewsPresenter();
        newsPresenter.getNews(getActivity(),this,tab.type,"1");
    }

    @Override
    public void onNext(NewsData data) {
     //   Log.i("tab","NewsData="+data.toString());
        recyclerView.setAdapter(new SuperAdapter(getActivity(),data.getPagebean().getContentlist(),0) {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public void onBind(Object holder, int viewType, int layoutPosition,NewsData.PagebeanBean.ContentlistBean item) {

            }
        });
    }
}
