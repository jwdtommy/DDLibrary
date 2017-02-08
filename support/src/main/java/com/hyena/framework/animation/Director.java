package com.hyena.framework.animation;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.hyena.framework.animation.texture.BitmapManager;
import com.hyena.framework.animation.utils.UIUtils;
import com.hyena.framework.clientlog.LogUtil;

import java.util.Stack;

/**
 * 导演
 *
 * @author yangzc
 */
public class Director implements RenderView.SizeChangeListener, OnTouchListener {

    private volatile boolean mPaused = true;
    protected RenderView mRenderView = null;
    private Rect mRect = new Rect();
    private Stack<CScene> mScenes;

    private BitmapManager mBitmapManager;

    private Handler mLooperHandler;
    private Context mContext;

    private int mTargetDensity = 320;

    public Director(Context context) {
        this.mContext = context;
        mScenes = new Stack<CScene>();
        mBitmapManager = BitmapManager.create();

        HandlerThread thread = new HandlerThread("io_framework_handler_anim");
        thread.start();

        mLooperHandler = new Handler(thread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleLooperMessage(msg);
            }

        };
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * 设置目标尺寸
     * @param density
     */
    public void setDensity(int density){
        this.mTargetDensity = density;
    }

    /**
     * 设置关联的View
     *
     * @param view
     */
    public void setRenderView(RenderView view) {
        mRenderView = view;
        if (view == null)
            return;

        mRenderView.setDirector(this);
        mRenderView.setSizeChangeListener(this);
        mRenderView.setOnTouchListener(this);
    }

    /**
     * 开启引擎
     */
    public void start() {
        mLooperHandler.removeMessages(MSG_LOOPER);
        mLooperHandler.obtainMessage(MSG_LOOPER).sendToTarget();
        if (mRenderView != null) {
            mRenderView.startRefresh();
        }
        mPaused = false;
    }

    /**
     * 关闭引擎
     */
    public void stop() {
        mLooperHandler.removeMessages(MSG_LOOPER);
        if (mRenderView != null) {
            mRenderView.stopRefresh();
        }
        mPaused = true;
    }

    /**
     * 引擎是否暂停
     *
     * @return
     */
    public boolean isPaused() {
        return mPaused;
    }

    /**
     * 释放资源
     */
    public void release() {
        //停止looper
        stop();
        mRenderView = null;
        //关闭场景
        if (mScenes != null && !mScenes.isEmpty()) {
            for (int i = 0; i < mScenes.size(); i++) {
                CScene scene = mScenes.get(i);
                scene.onScenePause();
                scene.onSceneStop();
            }
            mScenes.clear();
        }
        mCurrentScene = null;
        mPaused = true;
    }

    private CScene mCurrentScene;

    /**
     * 显示场景
     *
     * @param scene
     */
    public synchronized void showScene(CScene scene) {
        if (scene == null)
            return;

        if (mCurrentScene != null) {
            mCurrentScene.onScenePause();
        }

        this.mCurrentScene = scene;
        mScenes.push(mCurrentScene);

        mCurrentScene.onSceneStart();
        mCurrentScene.onSceneResume();
    }

    /**
     * 关闭场景
     */
    public synchronized void closeTopScene() {
        if (mScenes != null && mScenes.size() > 0) {
            CScene scene = mScenes.pop();
            if (scene != null) {
                scene.onScenePause();
                scene.onSceneStop();
            }
            if (mScenes.size() > 0) {
                mCurrentScene = mScenes.peek();
                mCurrentScene.onSceneResume();
            }
        }
    }

    /**
     * 删除场景
     *
     * @param scene
     */
    public synchronized void removeScene(CScene scene) {
        if (scene != null) {
            if (scene == mScenes.peek()) {
                closeTopScene();
            } else {
                mScenes.remove(scene);
                scene.onScenePause();
                scene.onSceneStop();
            }
        }
    }

    /**
     * 暂停场景
     */
    public synchronized void pauseScene() {
        if (mCurrentScene != null) {
            mCurrentScene.onScenePause();
        }
    }

    /**
     * 重启场景
     */
    public synchronized void resumeScene() {
        if (isPaused())
            return;

        if (mCurrentScene != null) {
            mCurrentScene.onSceneResume();
        }
    }

    /**
     * 获得活跃的场景
     *
     * @return
     */
    public CScene getActiveScene() {
        return mCurrentScene;
    }

    /**
     * 类型转换
     *
     * @param dpValue
     * @return
     */
    public int getPxValue(float dpValue) {
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        return (int) (dpValue * screenWidth / mTargetDensity);
    }

    /**
     * 获得当前view大小
     *
     * @return
     */
    public Rect getViewSize() {
        return mRect;
    }

    /**
     * 设置当前窗口size
     *
     * @param rect
     */
    public void setViewSize(Rect rect) {
        mRect.set(rect);
        if (mCurrentScene != null) {
            mCurrentScene.onSizeChange(mRenderView, mRect);
        }
    }

    /**
     * 重构是否显示
     *
     * @return
     */
    public boolean isViewVisible() {
        return (mRect != null && mRect.width() > 0 && mRect.height() > 0
                && mRenderView != null && getActiveScene() != null && mRenderView.isShown());
    }

    @Override
    public void onSizeChange(Rect rect) {
        setViewSize(rect);
        stop();
        CScene scene = getActiveScene();
        if (scene != null) {
            start();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CScene scene = getActiveScene();
        if (scene != null) {
            scene.dispatchTouchEvent(event);
        }
        return true;
    }

    private static final int MSG_LOOPER = 1;

    private void handleLooperMessage(Message msg) {
        int what = msg.what;
        switch (what) {
            case MSG_LOOPER: {
//                LogUtil.v("Director", "refresh");
                CScene scene = getActiveScene();
                if (scene != null) {
                    scene.refresh();
                }

                mLooperHandler.removeMessages(MSG_LOOPER);
                Message next = mLooperHandler.obtainMessage(MSG_LOOPER);
                mLooperHandler.sendMessageDelayed(next, getRefreshDelay());
                break;
            }
        }
    }

    private int mRefreshSpan;

    public void setRefreshDelay(int refreshSpan) {
        this.mRefreshSpan = refreshSpan;
    }

    public int getRefreshDelay() {
        int delay = EngineConfig.MIN_REFRESH_DELAY;
        if (mRefreshSpan > 0) {
            delay = mRefreshSpan;
        }
        return delay;
    }

    public BitmapManager getBitmapManager() {
        return mBitmapManager;
    }
}
