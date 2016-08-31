package fakefans.dd.com.fakefans.business.home;
import org.byteam.superadapter.animation.SlideInBottomAnimation;
import fakefans.dd.com.fakefans.Subscriber.SubscriberOnNextListener;
import fakefans.dd.com.fakefans.business.News.NewsAdapter;
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
    private NewsAdapter newsAdapter;
    public static final String KEY_TAB = "key_tab";
    private int page = 1;

    @Override
    public void onShow() {
        super.onShow();
        tab = (Tabs) getArguments().getSerializable(KEY_TAB);
        newsPresenter = new NewsPresenter();
        newsPresenter.getNews(getActivity(), this, tab.type, page + "");
        //  adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshData() {
        page = 1;
        newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }

    @Override
    public void onNext(NewsData data) {

        swipeRefreshLayout.setRefreshing(false);
        if (page == 1) {
            newsAdapter = new NewsAdapter(getActivity(), data.getPagebean().getContentlist());
            newsAdapter.setOnlyOnce(false);
            recyclerView.setAdapter(newsAdapter);
        } else {
            newsAdapter.addAll(data.getPagebean().getContentlist());
        }
    }

    @Override
    public void onLoadMoreData() {
        page++;
        newsPresenter.getNews(getActivity(), this, tab.type, page + "");
    }

}
