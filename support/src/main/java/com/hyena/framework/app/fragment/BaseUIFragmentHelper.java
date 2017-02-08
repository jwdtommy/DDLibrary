/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.app.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyena.framework.app.widget.CommonRefreshableFooter;
import com.hyena.framework.app.widget.CommonRefreshableHeader;
import com.hyena.framework.app.widget.AbsRefreshablePanel;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.BaseService;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * BaseUIFragment帮助接口
 * @author yangzc on 15/8/22.
 */
public class BaseUIFragmentHelper {

    private BaseUIFragment<?> mBaseUIFragment;

    public BaseUIFragmentHelper(BaseUIFragment<?> fragment){
        this.mBaseUIFragment = fragment;
    }

    public BaseUIFragment<?> getBaseUIFragment(){
        return mBaseUIFragment;
    }

    /**
     * 是否对用户可见
     */
    public void setVisibleToUser(boolean visible) {}

    /**
     * 背景颜色
     */
    public int getBackGroundColor(){
    	return 0xfff6f6f6;
    }


    private ImageView mIvCopyImageView;

    /**
     * 显示预览图片
     */
    public void showPicture(Rect rect, String url) {
        mIvCopyImageView = new ImageView(getBaseUIFragment().getActivity());
        mIvCopyImageView.setVisibility(View.VISIBLE);
        mIvCopyImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(rect.width(), rect.height());
        params.leftMargin = rect.left;
        params.topMargin = rect.top;
        mIvCopyImageView.setBackgroundColor(Color.RED);
        getBaseUIFragment().getRootView().addView(mIvCopyImageView, params);

//        showPicture(mIvCopyImageView, url);
    }

    /**
     * 表示图片预览是否对用户可见
     */
    public void setPreviewVisible2User(boolean visible){
        LogUtil.v("yangzc", "setPreviewVisible2User --> " + visible);
        if (mIvCopyImageView != null && !visible) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    getBaseUIFragment().getRootView().removeView(mIvCopyImageView);
                    mIvCopyImageView = null;
                }
            });
        }
    }

    /**
     * 显示预览图片
     */
    public void showPicture(final ImageView imageView, String url) {
        if (imageView == null || getBaseUIFragment() == null
                || getBaseUIFragment().getRootView() == null)
            return;

        ImageFetcher.getImageFetcher().loadImage(url, null, url, new ImageFetcher.ImageFetcherListener() {
            @Override
            public void onLoadComplete(final String imageUrl, final Bitmap bitmap, Object object) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    setPreviewVisible2User(true);
                    //create photo panel
                    RelativeLayout photoPanel = createPhotoPanel();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    //add photo panel
                    getBaseUIFragment().getRootView().addView(photoPanel, params);
                    photoPanel.setBackgroundColor(Color.BLACK);
                    //Ghost new imageView
                    final ImageView ghostImageView = new ImageView(getBaseUIFragment().getActivity());
                    ghostImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    photoPanel.addView(ghostImageView, imageView.getLayoutParams());
                    ghostImageView.setImageBitmap(bitmap);
                    ghostImageView.setScaleType(imageView.getScaleType());
                    //开始播放入场动画
                    startGhostInAnimator(imageView, ghostImageView, bitmap, photoPanel);
                }
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });
    }

    /**
     *c create
     */
    public RelativeLayout createPhotoPanel() {
        RelativeLayout photoPanel = new RelativeLayout(getBaseUIFragment().getActivity());
        return photoPanel;
    }

    private void startGhostOutAnimator(final ImageView rawImageView, final Bitmap bitmap
            , final ImageView ghostImageView, final RelativeLayout photoPanel) {
        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight();

        PhotoViewAttacher photoViewAttacher = (PhotoViewAttacher) ghostImageView.getTag();
        if (photoViewAttacher != null) {
            photoViewAttacher.cleanup();
            ghostImageView.setScaleType(rawImageView.getScaleType());
        }

        final int xy[] = new int[2];
        rawImageView.getLocationOnScreen(xy);
        final float currentScale = Math.min((screenWidth + 0.0f) / bitmap.getWidth(),
                (screenHeight + 0.0f) / bitmap.getHeight());
        final float currentWidth = bitmap.getWidth() * currentScale;
        final float currentHeight = bitmap.getHeight() * currentScale;
        final float currentX = (screenWidth - currentWidth)/2;
        final float currentY = (screenHeight - currentHeight)/2;
        ViewHelper.setX(ghostImageView, currentX);
        ViewHelper.setY(ghostImageView, currentY);
        ghostImageView.getLayoutParams().width = (int) currentWidth;
        ghostImageView.getLayoutParams().height = (int) currentHeight;
        ghostImageView.requestLayout();

        final float finalWidth = rawImageView.getWidth();
        final float finalHeight = rawImageView.getHeight();
        final float finalX = xy[0];
        final float finalY = xy[1];

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                getBaseUIFragment().getRootView().removeView(photoPanel);
                setPreviewVisible2User(false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                getBaseUIFragment().getRootView().removeView(photoPanel);
                setPreviewVisible2User(false);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                float x = currentX + (finalX - currentX) * value;
                float y = currentY + (finalY - currentY) * value;
                ViewHelper.setTranslationX(ghostImageView, x);
                ViewHelper.setTranslationY(ghostImageView, y);

                ghostImageView.getLayoutParams().width = (int) (currentWidth + (finalWidth - currentWidth) * value);
                ghostImageView.getLayoutParams().height = (int) (currentHeight + (finalHeight - currentHeight) * value);
                ghostImageView.requestLayout();

                ViewHelper.setAlpha(photoPanel, 1.0f - value);
            }
        };
        valueAnimator.addListener(listener);
        valueAnimator.addUpdateListener(listener);
        valueAnimator.start();
    }

    /**
     * start ghostIn animator
     */
    private void startGhostInAnimator(final ImageView rawImageView, final ImageView ghostImageView
            , final Bitmap bitmap, final RelativeLayout photoPanel) {
        final int xy[] = new int[2];
        rawImageView.getLocationOnScreen(xy);
        //init animator position
        ViewHelper.setTranslationX(ghostImageView, xy[0]);
        ViewHelper.setTranslationY(ghostImageView, xy[1]);

        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight();

        final int startX = xy[0];
        final int startY = xy[1];
        final int startWidth = rawImageView.getWidth();
        final int startHeight = rawImageView.getHeight();

        float bitmapScale = Math.min((screenWidth + 0.0f) / bitmap.getWidth(),
                (screenHeight + 0.0f) / bitmap.getHeight());
        final float finalWidth = bitmap.getWidth() * bitmapScale;
        final float finalHeight = bitmap.getHeight() * bitmapScale;
        final float finalX = (screenWidth - finalWidth)/2;
        final float finalY = (screenHeight - finalHeight)/2;


        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setAnimatorInStartAction(ghostImageView, xy);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setAnimatorInEndAction(rawImageView, ghostImageView, bitmap, photoPanel);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                setAnimatorInEndAction(rawImageView, ghostImageView, bitmap, photoPanel);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                int x = (int) ((finalX - startX) * value + startX);
                int y = (int) ((finalY - startY) * value + startY);
                ViewHelper.setTranslationX(ghostImageView, x);
                ViewHelper.setTranslationY(ghostImageView, y);

                ghostImageView.getLayoutParams().width = (int) (startWidth + (finalWidth - startWidth) * value);
                ghostImageView.getLayoutParams().height = (int) (startHeight + (finalHeight - startHeight) * value);
                ghostImageView.requestLayout();

                ViewHelper.setAlpha(photoPanel, value);
            }
        };
        animator.addListener(listener);
        animator.addUpdateListener(listener);
        animator.start();
    }

    /**
     * run on animatorIn start
     */
    private void setAnimatorInStartAction(ImageView ghostImageView, int xy[]){
        ViewHelper.setTranslationX(ghostImageView, xy[0]);
        ViewHelper.setTranslationY(ghostImageView, xy[1]);
    }

    /**
     * run on animatorIn end
     */
    private void setAnimatorInEndAction(final ImageView rawImageView, final ImageView ghostImageView
            , final Bitmap bitmap, final RelativeLayout photoPanel) {
        ViewHelper.setTranslationX(ghostImageView, 0);
        ViewHelper.setTranslationY(ghostImageView, 0);
        ViewHelper.setAlpha(ghostImageView, 1.0f);
        ViewHelper.setScaleX(ghostImageView, 1.0f);
        ViewHelper.setScaleY(ghostImageView, 1.0f);
        ghostImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(ghostImageView);
        photoViewAttacher.update();
        photoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                startGhostOutAnimator(rawImageView, bitmap, ghostImageView, photoPanel);
            }
        });
        ghostImageView.setTag(photoViewAttacher);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getBaseUIFragment().getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getBaseUIFragment().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * replace fragment
     */
    public void replaceFragment(int id, BaseUIFragment fragment) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return;

        FragmentTransaction transaction = getBaseUIFragment()
                .getChildFragmentManager().beginTransaction();
        transaction.replace(id, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * showPushFragment
     */
    public void showPushFragment(Class<? extends BaseUIFragment> clz) {
        showPushFragment(clz, null);
    }

    /**
     * showPopFragment
     */
    public void showPopFragment(Class<? extends BaseUIFragment> clz) {
        showPopFragment(clz, null);
    }

    /**
     * showPushFragment
     */
    public void showPushFragment(Class<? extends BaseUIFragment> clz, Bundle bundle) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return;

        BaseUIFragment fragment = BaseUIFragment.newFragment(
                getBaseUIFragment().getActivity(), clz, bundle);
        getBaseUIFragment().showPushFragment(fragment);
    }

    /**
     * showPopFragment
     */
    public void showPopFragment(Class<? extends BaseUIFragment> clz, Bundle bundle) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return;

        BaseUIFragment fragment = BaseUIFragment.newFragment(
                getBaseUIFragment().getActivity(), clz, bundle);
        getBaseUIFragment().showPopFragment(fragment);

    }

    public <T extends BaseService> T getService(String serviceName) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return null;
        return (T) mBaseUIFragment.getSystemService(serviceName);
    }


    public void keyBoardAdjustSpan(int height, int rawHeight){
        int scrollY = rawHeight - height;
//        if (mBaseUIFragment.isEnableScroll()) {
        if (mBaseUIFragment.getContentView() != null)
            mBaseUIFragment.getContentView().scrollTo(0, scrollY);
//        } else {
//            if (mBaseUIFragment.getContentView() != null) {
//                mBaseUIFragment.getContentView().scrollTo(0, scrollY);
//            }
//        }
    }

    public void keyBoardAdjustResize(int height, int rawHeight) {
//        if (mBaseUIFragment.isEnableScroll()) {
        if (mBaseUIFragment.getContentView() != null) {
            mBaseUIFragment.getContentView().getLayoutParams().height = height;
            mBaseUIFragment.getContentView().requestLayout();
        }
//        } else {
//            if (mBaseUIFragment.getContentView() != null) {
//                mBaseUIFragment.getContentView().getLayoutParams().height = height;
//                mBaseUIFragment.getContentView().requestLayout();
//            }
//        }
    }

    public AbsRefreshablePanel buildRefreshableLayoutHeader() {
        return new CommonRefreshableHeader(getBaseUIFragment().getActivity());
    }

    public AbsRefreshablePanel buildRefreshableLayoutFooter() {
        return new CommonRefreshableFooter(getBaseUIFragment().getActivity());
    }
}
