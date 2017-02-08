/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hyena.framework.annotation.AttachViewId;
import com.hyena.framework.annotation.LayoutAnimation;
import com.hyena.framework.annotation.SystemService;
import com.hyena.framework.app.NavigateController;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.app.widget.BaseUIRootLayout;
import com.hyena.framework.app.widget.EmptyView;
import com.hyena.framework.app.widget.AbsRefreshablePanel;
import com.hyena.framework.app.widget.LoadingView;
import com.hyena.framework.app.widget.RefreshableLayout;
import com.hyena.framework.app.widget.TitleBar;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.ResourceUtils;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UIFragment基础类
 * @author yangzc
 */
public class BaseUIFragment<T extends BaseUIFragmentHelper> extends BaseSubFragment {

    private static final String TAG = "BaseUIFragment";

    public static final int ACTION_DEFAULT = 0;
    public static final int PAGE_FIRST = 1;
    public static final int PAGE_MORE = 2;

	private TitleBar mTitleBar;
	private LoadingView mLoadingView;
	private EmptyView mEmptyView;
    private View mContentView;
    //根布局
    private BaseUIRootLayout mRootView;

    // 当前数据获取任务
    private DataLoaderTask mDataLoaderTask;
	
	public static final int STYLE_WITH_TITLE = 0;//有标题样式
	public static final int STYLE_NO_TITLE = 1;//无标题样式
	//标题样式
	private int mTitleStyle = STYLE_WITH_TITLE;
	//是否内容panel添加滚动条
	private boolean mIsAddScrollView = false;
    //是否内容panel添加refreshableLayout
    private boolean mIsAddRefreshableLayout = false;

    private T mUIFragmentHelper;
    //是否对用户可见
    private boolean mVisible = false;
    //是否初始化成功
    private boolean mInited = false;
    private boolean mFinishing = false;
    private int mAncherX, mAncherY;

    private int mTitleBarId;
    private int mStatusBarId;
    private boolean mStatusBarEnable = false;
    private int mStatusBarColor = Color.TRANSPARENT;
    private TextView mTvStatusBar;

    //入场动画类型
    private AnimType mAnimType = AnimType.RIGHT_TO_LEFT;
    //入场动画
    public static enum AnimType {
        ANIM_NONE //没有动画
        , RIGHT_TO_LEFT//右到左
        , BOTTOM_TO_TOP//下到上
        ;
    }

    /**
     * 设置标题样式
     */
    public void setTitleStyle(int style){
        this.mTitleStyle = style;
    }
    
    /**
     * 是否支持滚动
     */
    public void setEnableScroll(boolean scroll){
    	this.mIsAddScrollView = scroll;
    }

    /**
     * 是否支持滚动条
     */
    public boolean isEnableScroll() {
        return mIsAddScrollView;
    }

    public void addRefreshableLayout(boolean isAdd) {
        this.mIsAddRefreshableLayout = isAdd;
    }

    public RefreshableLayout getRefreshLayout() {
        return (RefreshableLayout) getContentView();
    }

    /**
     * 设置UI帮助类
     */
    public void setUIFragmentHelper(T helper){
        this.mUIFragmentHelper = helper;
    }

    /**
     * 设置入场动画类型
     */
    public void setAnimationType(AnimType type){
        this.mAnimType = type;
    }

    public void setStatusTintBarEnable(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.mStatusBarEnable = enable;
            if (mTvStatusBar != null) {
                mTvStatusBar.setVisibility(enable? View.VISIBLE : View.GONE);
            }
        }
    }

    public boolean isStatusBarTintEnabled(){
        return mStatusBarEnable;
    }

    public void setStatusTintBarColor(int color) {
        this.mStatusBarColor = color;
        if (mTvStatusBar != null) {
            mTvStatusBar.setBackgroundColor(color);
        }
    }

    /**
     * 获得UI帮助类
     * @return
     */
    public T getUIFragmentHelper(){
        if (mUIFragmentHelper == null) {
            if (getActivity() == null) {
                return null;
            }

            if (getActivity() instanceof NavigateController) {
                mUIFragmentHelper = ((NavigateController)getActivity()).getUIFragmentHelper(this);
            }
        }
        return mUIFragmentHelper;
    }

    /**
     * 初始化新的Fragment
     * @param activity 依赖的Activity
     * @param cls Fragment类
     * @param bundle 参数
     * @param <T> 生成的Fragment
     * @return
     */
    public static <T extends BaseUIFragment<?>> T newFragment(Activity activity,
                                               Class<?> cls, Bundle bundle){
        return newFragment(activity, cls, bundle, AnimType.RIGHT_TO_LEFT);
    }

    /**
     * 初始化新的Fragment
     * @param activity 依赖的Activity
     * @param cls Fragment类
     * @param bundle 参数
     * @param <T> 生成的Fragment
     * @param animType 入场动画类型
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends BaseUIFragment> T newFragment(Activity activity,
                           Class cls, Bundle bundle, AnimType animType){
        T fragment = (T) Fragment.instantiate(activity, cls.getName(), bundle);
        //设置导航控制器
        if(activity instanceof NavigateController) {
            NavigateController controller = (NavigateController) activity;
            fragment.setArguments(controller, null);
            fragment.setAnimationType(animType);
            //设置导航帮助类
            fragment.setUIFragmentHelper(controller.getUIFragmentHelper(fragment));
        }
        return fragment;
    }

    @Override
    public BaseSubFragment setArguments(NavigateController negativeController,
                        BaseSubFragment parentFragment) {
        if (negativeController != null) {
            mUIFragmentHelper = negativeController.getUIFragmentHelper(this);
        }
        return super.setArguments(negativeController, parentFragment);
    }

    @Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		// 窗口自适应键盘
		getActivity()
				.getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mStatusBarId = ResourceUtils.getId("common_status_bar");
        mTitleBarId = ResourceUtils.getId("common_title_bar");

        resetTask();
        autoAttachAllService();
	}

    /**
     * 生成默认的跟布局
     */
    public BaseUIRootLayout newUIRootLayout() {
        return new BaseUIRootLayout(getActivity());
    }
	
	/**
	 * 创建View
	 * @param savedInstanceState
	 * @return
	 */
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return null;
	}

	@Override
	public final View onCreateViewImpl(ViewGroup container, Bundle savedInstanceState) {
        mRootView = newUIRootLayout();
        mRootView.setClickable(true);

        //生成状态栏
        mTvStatusBar = new TextView(getActivity());
        mTvStatusBar.setId(mStatusBarId);
        mTvStatusBar.setBackgroundColor(mStatusBarColor);
        RelativeLayout.LayoutParams statusBarParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ResourceUtils.getInternalDimensionSize("status_bar_height"));
        mRootView.addView(mTvStatusBar, statusBarParams);
        mTvStatusBar.setVisibility(isStatusBarTintEnabled() ? View.VISIBLE : View.GONE);

		if(mTitleStyle == STYLE_WITH_TITLE){
            //添加TitleBar
			mTitleBar = UIViewFactory.getViewFactory().buildTitleBar(this);
			mTitleBar.setId(mTitleBarId);
			LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, UIUtils.dip2px(50));
            titleParams.addRule(RelativeLayout.BELOW, mStatusBarId);
            mRootView.addView(mTitleBar, titleParams);
            mTitleBar.setVisibility(View.GONE);

            mTitleBar.setTitleBarListener(mTitleBarListener);
            //初始化菜单
            initMenus();
		}

        //添加EmptyView
        LayoutParams emptyParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if(mTitleStyle == STYLE_WITH_TITLE) {
            emptyParams.addRule(RelativeLayout.BELOW, mTitleBarId);
        } else {
            emptyParams.addRule(RelativeLayout.BELOW, mStatusBarId);
        }

		mEmptyView = UIViewFactory.getViewFactory().buildEmptyView(this);
        mRootView.addView(mEmptyView, emptyParams);
        mEmptyView.setVisibility(View.GONE);

        //添加LoadingView
        LayoutParams loadingParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if(mTitleStyle == STYLE_WITH_TITLE) {
        	loadingParams.addRule(RelativeLayout.BELOW, mTitleBarId);
        } else {
            loadingParams.addRule(RelativeLayout.BELOW, mStatusBarId);
        }
        mLoadingView = UIViewFactory.getViewFactory().buildLoadingView(this);
        mRootView.addView(mLoadingView, loadingParams);
        mLoadingView.setVisibility(View.GONE);

        //添加内容View
        mContentView = onCreateViewImpl(savedInstanceState);
        LayoutParams contentParams = new LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (mContentView != null) {
            if (mContentView.getBackground() == null) {
            	int color = 0xfff6f6f6;
            	if(mUIFragmentHelper != null) {
            		color = mUIFragmentHelper.getBackGroundColor();
            	}
                mContentView.setBackgroundColor(color);
            }
            mContentView.setClickable(true);
            View contentView = mContentView;
            if (mIsAddScrollView) {//support scrollView
                ScrollView scrollView = new ScrollView(getActivity());
                scrollView.setFillViewport(true);
                scrollView.setVerticalScrollBarEnabled(false);
                scrollView.setHorizontalScrollBarEnabled(false);
            	scrollView.addView(mContentView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            	contentView = scrollView;
    		}

            if (mIsAddRefreshableLayout) {//support refresh or load more
                RefreshableLayout refreshableLayout = new RefreshableLayout(getActivity());
                refreshableLayout.addView(contentView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT));
                refreshableLayout.setHeaderPanel(buildRefreshableLayoutHeader());
                refreshableLayout.setFooterPanel(buildRefreshableLayoutFooter());
                contentView = refreshableLayout;
            }
            this.mContentView = contentView;
            
            if(mTitleStyle == STYLE_WITH_TITLE){
                contentParams.addRule(RelativeLayout.BELOW, mTitleBarId);
                mRootView.addView(contentView, 2, contentParams);
            }else{
                contentParams.addRule(RelativeLayout.BELOW, mStatusBarId);
                mRootView.addView(contentView, 1, contentParams);
            }
        }
        mInited = true;
        //注册数据监听器
        registReceiver();
		return mRootView;
	}

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        autoAttachAllView();
        autoAttachAnimation();
        if (!isSlideable() && getAnimationIn() == null) {
            onPanelOpened(view);
        }
    }
    
    @Override
    public void onDestroyViewImpl() {
    	super.onDestroyViewImpl();
    	//解注册数据监听器
    	unRegistReceiver();
    	mInited = false;
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        releaseTask();
    }

    @Override
    public void onPanelOpened(View pPanel) {
        super.onPanelOpened(pPanel);
        LogUtil.v(TAG, "onPanelOpened:" + getClass().getSimpleName());
    }

    @Override
    public void onPanelClosed(View pPanel) {
        super.onPanelClosed(pPanel);
        LogUtil.v(TAG, "onPanelClosed:" + getClass().getSimpleName());
    }

    public boolean isInited(){
        return mInited;
    }
    
    private boolean isLazyLoaded = false;

    public void setLazyLoad() {
        isLazyLoaded = true;
    }

    /**
     * ViewPager中Fragment延迟加载使用，其他情况下请不要使用 </p>
     * 
     * 延迟加载 </p>
     */
    protected void onLazyLoad() {
        isLazyLoaded = false;
    }

    private boolean mLazyLoaded = false;
    public void doLazyLoad(){
        if (mLazyLoaded)
            return;

        mLazyLoaded = true;
        onLazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
    	super.setUserVisibleHint(isVisibleToUser);
    	setVisibleToUser(isVisibleToUser);
    }

    @Override
    public void setVisibleToUser(boolean visible) {
    	if (visible == mVisible) {
			return;
		}
        super.setVisibleToUser(visible);
        this.mVisible = visible;
        
        if(mUIFragmentHelper != null) {
            mUIFragmentHelper.setVisibleToUser(visible);
        }
    }
    
    @Override
    public void onError(Throwable e) {
    	if (LogUtil.isDebug()) {
//    		LogUtil.e(((Object)this).getClass().getSimpleName(), e);
            throw new RuntimeException(e);
		} else {
			super.onError(e);
		}
    }

    /**
     * build refreshable header
     */
    public AbsRefreshablePanel buildRefreshableLayoutHeader() {
        if (mUIFragmentHelper == null)
            return null;

        return mUIFragmentHelper.buildRefreshableLayoutHeader();
    }

    /**
     * build refreshable footer
     */
    public AbsRefreshablePanel buildRefreshableLayoutFooter() {
        if (mUIFragmentHelper == null)
            return null;
        return mUIFragmentHelper.buildRefreshableLayoutFooter();
    }

    /**
     * 获得系统服务
     * @param name
     * @return
     */
    public Object getSystemService(String name) {
        IServiceManager manager = ServiceProvider.getServiceProvider()
                .getServiceManager();
        if (manager != null) {
            Object service = manager.getService(name);
            if (service != null)
                return service;
        }
        return getActivity().getSystemService(name);
    }
    
    /**
     * 获得入场动画
     * @return
     */
    protected Animation getAnimationIn(){
        if (mAnimType == AnimType.RIGHT_TO_LEFT) {
            Animation animation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, 0);
            animation.setDuration(200);
            animation.setInterpolator(new AccelerateInterpolator());
            return animation;
        } else if(mAnimType == AnimType.BOTTOM_TO_TOP){
            Animation animation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0);
            animation.setDuration(200);
            animation.setInterpolator(new AccelerateInterpolator());
            return animation;
        }
    	return null;
    }

    /**
     * 获得出场动画
     * @return
     */
    protected Animation getAnimationOut(){
    	//对用户不可见，则不执行出场动画
    	if (!mVisible || !isVisible()) {
			return null;
		}
        if (mAnimType == AnimType.BOTTOM_TO_TOP) {
            Animation animation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 1.0f);
            animation.setDuration(200);
            animation.setInterpolator(new AccelerateInterpolator());
            return animation;
        }
        return null;
    }

    public boolean isFinishing() {
        return mFinishing;
    }

    @Override
    public void finish() {
        if (mFinishing)
            return;

        mFinishing = true;
        UIUtils.hideInputMethod(getActivity());

        Animation animation = getAnimationOut();
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    BaseUIFragment.this.onPanelClosed(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            getView().startAnimation(animation);
        } else {
            if (!isSlideable()) {
                onPanelClosed(null);
            }
            super.finish();
        }
    }


    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation animation = null;
        if (enter) {
            animation = getAnimationIn();
        }

        if(animation == null)
            return null;

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                if (enter) {
                    BaseUIFragment.this.onPanelOpened(null);
                } else {
                    BaseUIFragment.this.onPanelClosed(null);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return animation;
    }

    //============================ UI导航部分 ===================================================

    /**
     * 打开从右向左的Fragment
     * @param fragment
     */
    public void showPushFragment(BaseUIFragment<?> fragment){
        fragment.setAnimationType(AnimType.RIGHT_TO_LEFT);
        showFragment(fragment);
    }

    /**
     * 打开弹出Fragment
     * @param fragment
     */
    public void showPopFragment(BaseUIFragment<?> fragment){
        fragment.setAnimationType(AnimType.BOTTOM_TO_TOP);
        showFragment(fragment);
    }

    @Override
    public void showFragment(BaseSubFragment fragment) {
        super.showFragment(fragment);
    }

    public void showFragment(BaseUIFragment<?> fragment, int ancherX, int ancherY) {
        this.mAncherX = ancherX;
        this.mAncherY = ancherY;
        super.showFragment(fragment);
    }

    //============================ 相关回调 ==================================================

    //titleBar事件回调
    private TitleBar.TitleBarListener mTitleBarListener = new TitleBar.TitleBarListener() {

        @Override
        public void onBackPressed(View view) {
            finish();
        }

        @Override
        public void onTitlePressed(View view) {

        }

        @Override
        public void onMenuSelected(MenuItem menu) {
            onMenuItemClick(menu);
        }
    };

    //============================ 菜单相关 ==================================================

    public void refreshMenus(){
        //注册菜单项到标题栏
        getTitleBar().setMenuItems(getMenuItems());
    }
    
    /**
     * 初始化菜单
     */
    private void initMenus(){
        //注册菜单项到标题栏
        getTitleBar().setMenuItems(getMenuItems());
    }

    /**
     * 获得菜单选项
     * @return
     */
    public List<MenuItem> getMenuItems() {
        return null;
    }

    /**
     * 菜单选中
     * @param item
     */
    public void onMenuItemClick(MenuItem item) {}

    //============================ 获得通用View ==================================================

    /**
     * 获得LoadingView
     * @return
     */
    public LoadingView getLoadingView(){
        return mLoadingView;
    }

    /**
     * 获得EmptyView
     * @return
     */
    public EmptyView getEmptyView(){
        return mEmptyView;
    }

    /**
     * 获得标题栏
     * @return
     */
    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    /**
     * 获得内容View
     * @return
     */
    public View getContentView(){
        return mContentView;
    }

    /**
     * 获得根ViewGrop
     * @return
     */
    public BaseUIRootLayout getRootView(){
        return mRootView;
    }

    /**
     * 显示内容
     */
    public void showContent() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
            }
        });
    }

    //============================ 网络数据加载回调 ==================================================

    /**
     * 预加载</p>
     * @param action
     * @param pageNo
     */
    public void onPreAction(int action, int pageNo) {
    	if (pageNo == PAGE_FIRST) {
    		int color = 0xfff6f6f6;
        	if(mUIFragmentHelper != null) {
        		color = mUIFragmentHelper.getBackGroundColor();
        	}
			getLoadingView().setBackgroundColor(color);
		} else {
			getLoadingView().setBackgroundColor(Color.TRANSPARENT);
		}
        getLoadingView().showLoading();
    }

    /**
     * 执行后台任务</p>
     *
     * @param action
     * @param pageNo
     * @param params
     * @return
     */
    public BaseObject onProcess(final int action, final int pageNo, Object... params) {
    	//获得URL模型对
    	UrlModelPair urlModel = getRequestUrlModelPair(action, pageNo, params);
    	if (urlModel != null && !urlModel.isEmpty()) {
    		//根据URL模型对获取缓存，如果缓存存在且合法，则进行缓存获取成功回调
			final BaseObject object = new DataAcquirer<BaseObject>().acquireCache(urlModel.mUrl, 
					urlModel.mOnlineObject);
			if (object != null && object.isAvailable()) {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						onGetCache(action, pageNo, object);
					}
				});
			}
		}
        return null;
    }
    
    /**
     * 获得请求的URL数据Pair</p>
     * @param action
     * @param pageNo
     * @param params
     * @return
     */
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params){
    	return null;
    }
    
    /**
     * 获取数据成功</p>
     *
     * @param action
     * @param pageNo
     * @param result
     * @param params 请求参数
     */
    public void onGet(int action, int pageNo, BaseObject result, Object ...params) {
//    	//通知友员数据改变
//    	notifyFriendsDataChange();
//        showContent();
    	onGet(action, pageNo, result);
    }
    
    @Deprecated
    public void onGet(int action, int pageNo, BaseObject result) {
    	//通知友员数据改变
    	notifyFriendsDataChange();
        showContent();
    }
    
    /**
     * 获得缓存成功</p>
     * @param action
     * @param pageNo
     * @param result
     */
    public void onGetCache(int action, int pageNo, BaseObject result){
        showContent();
    }
    
    /**
     * 获取数据失败
     *
     * @param action
     * @param pageNo
     * @param result
     * @param params 请求参数
     */
    public void onFail(int action, int pageNo, BaseObject result, Object ...params) {
//    	onFailImpl(action, pageNo, result);
    	onFail(action, pageNo, result);
    }
    
    @Deprecated
    public void onFail(int action, int pageNo, BaseObject result){
    	onFailImpl(action, pageNo, result);
    }
    
    private void onFailImpl(int action, int pageNo, BaseObject result){
    	if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (pageNo > PAGE_FIRST) {
        	if(!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()){
                ToastUtils.showToast(getActivity(), "暂无网络请稍后再试!");
        	} else if (result != null && !TextUtils.isEmpty(result.getRawResult())) {
                String hint = ErrorManager.getErrorManager().getErrorHint(
                        result.getRawResult(), result.getErrorDescription());
                ToastUtils.showToast(getActivity(), hint);
            } else {
                ToastUtils.showToast(getActivity(), "获取数据失败!");
            }
            getLoadingView().setVisibility(View.GONE);
//            showContent();
        } else {
            if(!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()){
                getEmptyView().showNoNetwork();
                return;
            }
            if (result != null && !TextUtils.isEmpty(result.getRawResult())) {
                String hint = ErrorManager.getErrorManager().getErrorHint(result
                        .getRawResult(), result.getErrorDescription());
                getEmptyView().showEmpty(result.getRawResult(), hint);
            } else {
                getEmptyView().showEmpty("", "获取数据失败");
            }
        }
    }
    
    /**
     * Cancel请求
     */
    public void onCancel(int action, int pageNo) {
    	//显示内容页
    	showContent();
    }
    
    /**
     * 是否正在加载数据
     * @return
     */
    public boolean isLoading() {
    	if (mDataLoaderTask != null) {
			return mDataLoaderTask.isLoading();
		}
    	return false;
    }

    //============================ 网络数据加载部分 ==================================================

    private void releaseTask(){
        if (mExecutor != null) {
            mExecutor.shutdown();
        }
    }

    private void resetTask(){
        mExecutor = Executors.newFixedThreadPool(8);
    }

    private ExecutorService mExecutor = Executors.newFixedThreadPool(8);

    protected Executor getExecutor(){
        return mExecutor;
    }

    /**
     * 开始加载数据
     *
     * @param pageNo 页数
     * @param params 请求参数
     */
    public void loadDefaultData(int pageNo, Object... params) {
        if (isLazyLoaded)
            return;

        if (mDataLoaderTask != null) {
            mDataLoaderTask.cancel(false);
            mDataLoaderTask.handleCancel();
        }
        mDataLoaderTask = new DataLoaderTask(ACTION_DEFAULT, pageNo, params);
        if (Build.VERSION.SDK_INT >= 11) {
            mDataLoaderTask.executeOnExecutor(getExecutor());
        } else {
            mDataLoaderTask.execute();
        }
    }

    /**
     * 开始加载数据
     *
     * @param action 行为
     * @param pageNo 页数
     * @param params 参数
     */
    public void loadData(int action, int pageNo, Object... params) {
        if (isLazyLoaded)
            return;

        if (mDataLoaderTask != null) {
            mDataLoaderTask.cancel(true);
            mDataLoaderTask.handleCancel();
        }
        mDataLoaderTask = new DataLoaderTask(action, pageNo, params);
        if (Build.VERSION.SDK_INT >= 11) {
            mDataLoaderTask.executeOnExecutor(getExecutor());
        } else {
            mDataLoaderTask.execute();
        }
    }

    /**
     * 数据获取任务
     */
    private class DataLoaderTask extends AsyncTask<Object, Void, BaseObject> {
        private int mAction;
        private int mPageNo;
        private Object[] mParams;
        private boolean mLoading = false;

        public DataLoaderTask(int action, int pageNo, Object ...params) {
            super();
            this.mAction = action;
            this.mPageNo = pageNo;
            this.mParams = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading = true;
            try {
                onPreAction(mAction, mPageNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected BaseObject doInBackground(Object... params) {
            try {
            	//获得缓存
                return onProcess(mAction, mPageNo, mParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseObject result) {
            super.onPostExecute(result);
            mLoading = false;
            try {
            	if (getActivity() == null || getActivity().isFinishing()) {
					return;
				}
                if (result != null && result.isAvailable()) {
//                    onGet(mAction, mPageNo, result);
                    onGet(mAction, mPageNo, result, mParams);
                } else {
//                    onFail(mAction, mPageNo, result);
                    onFail(mAction, mPageNo, result, mParams);
                }
            } catch (Throwable e) {
                LogUtil.e("", e);
            }
        }
        
        /**
         * Cancel请求
         */
        public void handleCancel(){
            mLoading = false;
        	onCancel(mAction, mPageNo);
        }
        
        /**
         * 是否加载数据中
         * @return
         */
        public boolean isLoading() {
        	return mLoading;
        }
        
        @Override
        protected void onCancelled() {
        	super.onCancelled();
        	mLoading = false;
        }
    }

    //============================ 自动注入部分 ==================================================

    /**
     * 自动注入所有Field
     */
    private void autoAttachAllView() {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields == null)
                return;

            for (Field field : fields) {
                //自动注入View
                if (field.isAnnotationPresent(AttachViewId.class)) {
                    final AttachViewId inject = field.getAnnotation(AttachViewId.class);
                    int id = inject.value();
                    if (id > 0) {
                        field.setAccessible(true);
                        field.set(this, getView().findViewById(id));//给我们要找的字段设置值
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动注入所有服务
     */
    private void autoAttachAllService() {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields == null)
                return;
            for (Field field : fields) {
                if(field.isAnnotationPresent(SystemService.class)){//自动注入服务
                    SystemService service = field.getAnnotation(SystemService.class);
                    String systemName = service.value();
                    if(!TextUtils.isEmpty(systemName)) {
                        field.setAccessible(true);
                        field.set(this, getSystemService(systemName));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 关联动画
     */
    private void autoAttachAnimation() {
    	try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields == null)
                return;
            for (Field field : fields) {
                if(field.isAnnotationPresent(LayoutAnimation.class)){//自动注入服务
                	field.setAccessible(true);
                	Object value = field.get(this);
                	if (value instanceof View) {
                    	final LayoutAnimation animation = field.getAnnotation(LayoutAnimation.class);
						final View view = (View) value;
						ViewTreeObserver observer = view.getViewTreeObserver();
						observer.addOnPreDrawListener(new OnPreDrawListener() {
							@Override
							public boolean onPreDraw() {
								ViewTreeObserver currentVto = view.getViewTreeObserver();
								if (currentVto.isAlive()) {
									currentVto.removeOnPreDrawListener(this);
								}
								playAnimation(view, animation);
								return false;
							}
						});
					}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 播放动画
     * @param view
     * @param animation
     */
    private void playAnimation(View view, LayoutAnimation animation) {
    	Animator animatorX = ObjectAnimator.ofFloat(view, "translationX", -animation.offsetX(), 0);
    	Animator animatorY = ObjectAnimator.ofFloat(view, "translationY", -animation.offsetY(), 0);
    	AnimatorSet animatorSet = new AnimatorSet();
    	animatorSet.playTogether(animatorX, animatorY);
    	animatorSet.setDuration(animation.duration());
    	animatorSet.play(animatorSet);
    }

    //========================消息中心===================================
    public static final String MSG_PREFIX = "com.hyena.framework.app.fragment.";
    
    public Class<? extends BaseUIFragment<?>>[] getFriendsTags(Bundle bundle) {
    	return null;
    }

    /**
     * 通知友员数据改变
     */
    public void notifyFriendsDataChange(){
    	notifyFriendsDataChange(null);
    }
    
    /**
     * 通知友员数据改变
     */
    public void notifyFriendsDataChange(Bundle bundle){
    	Class<? extends BaseUIFragment<?>>[] friendsCls = getFriendsTags(bundle);
    	if (friendsCls != null && friendsCls.length > 0) {
    		if (bundle == null) {
				bundle = new Bundle();
			}
    		bundle.putString("args_action", getAction());
			for (int i = 0; i < friendsCls.length; i++) {
				Intent intent = new Intent(MSG_PREFIX + friendsCls[i].getSimpleName());
				intent.putExtras(bundle);
				MsgCenter.sendLocalBroadcast(intent);
			}
		}
    }
    
    /**
     * 获得当前场景的action
     * @return
     */
    public String getAction(){
    	return MSG_PREFIX + getClass().getSimpleName();
    }
    
    /**
     * 数则数据监听器
     */
    private void registReceiver(){
        IntentFilter globalFilter = new IntentFilter();
        globalFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        MsgCenter.registerGlobalReceiver(mBroadcastReceiver, globalFilter);

    	IntentFilter localFilter = new IntentFilter();
    	localFilter.addAction(getAction());
    	MsgCenter.registerLocalReceiver(mBroadcastReceiver, localFilter);
    }
    
    /**
     * 解注册数据监听器
     */
    private void unRegistReceiver(){
        MsgCenter.unRegisterGlobalReceiver(mBroadcastReceiver);
        MsgCenter.unRegisterLocalReceiver(mBroadcastReceiver);
    }
    
    /**
     * 接收数据成功
     * @param context
     * @param intent
     */
    private void onReceiveImpl(Context context, Intent intent){
    	String action = intent.getAction();
    	if (getAction().equals(action)) {
    		Bundle bundle = intent.getExtras();
    		if (bundle != null) {
    			
			}
    		onFriendsDataChange(intent);
		} else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
            onNetworkChange();
        }
    }

    /**
     * 网络状态改变
     */
    public void onNetworkChange(){
    }
    
    /**
     * 友员数据改变
     * @param intent
     */
    public void onFriendsDataChange(Intent intent) {}
    
    //监听器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				onReceiveImpl(context, intent);
			} catch (Exception e) {
				LogUtil.e("", e);
			}
		}
    };

    private int mLastHeight = 0;
    @Override
    public void onWindowVisibleSizeChange(Rect rect) {
        super.onWindowVisibleSizeChange(rect);
        if (getActivity() == null || getActivity().isFinishing())
            return;

        int height = rect.height();
        int rawHeight = getResources().getDisplayMetrics().heightPixels - rect.top;
        if (mTitleStyle == STYLE_WITH_TITLE) {
            height -= mTitleBar.getMeasuredHeight();
            rawHeight -= mTitleBar.getMeasuredHeight();
        }
        if (height == mLastHeight)
            return;

        onContentVisibleSizeChange(height, rawHeight);
        mLastHeight = height;
    }

    /**
     * 窗体高度发生变化
     * @param height 当前可分配的内容高度
     * @param rawHeight 原始可分配的内容高度
     */
    protected void onContentVisibleSizeChange(int height, int rawHeight) {}
}
