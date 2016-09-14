package fakefans.dd.com.fakefans.business.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import fakefans.dd.com.fakefans.R;
import fakefans.dd.com.fakefans.ui.base.BaseActivity;
import me.wangyuwei.particleview.ParticleView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class HomeActivity extends BaseActivity {

    @Bind(R.id.tab_FindFragment_title)
    TabLayout tabLayout;
    @Bind(R.id.vp_FindFragment_pager)
    ViewPager viewPager;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.pv)
    ParticleView particleView;
    private HomePagerAdapter homePagerAdapter;

    @Override
    public int onCreateView() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        RXDemo();
//        TopChannelPresenter topChannelPresenter=new TopChannelPresenter();
//        topChannelPresenter.getTopChannel(this,this);

    }

    private void init() {
        homePagerAdapter = new HomePagerAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
        particleView.startAnim();
        particleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        particleView.setVisibility(View.GONE);

                    }
                });
            }
        });
    }

    private void RXDemo() {
//        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                subscriber.onNext("AAA");
//            }
//        });
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {

            }
            @Override
            public void onNext(String s) {
            }
        };

        Observable<String> observable = Observable.just("生活不止眼前的苟且").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s + "---J.Tommy";
            }
        });
//        observable.map(new Func1<String, String>() {
//            @Override
//            public String call(String s) {
//                return s + "---J.Tommy";
//            }
//        });

        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                tv_name.setText(s);
            }
        };
        observable.subscribe(onNextAction);
    }

//    @Subscribe
//    public void onEvent(TopChannelEvent topChannelEvent) {
//        //设置TabLayout的模式
////        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        homePagerAdapter.notify(DataManager.topChannels);
//        viewPager.setAdapter(homePagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
//    };

//    @Override
//    public void onNext(List<TopChannel> data) {
//        homePagerAdapter.notify(data);
//        viewPager.setAdapter(homePagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
//    }

}
