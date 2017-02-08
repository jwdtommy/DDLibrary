package com.hyena.framework.app.fragment;

import com.hyena.framework.app.widget.HSlidingPaneLayout;
import com.hyena.framework.app.widget.HSlidingPaneLayout.PanelSlideListener;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

/**
 * 支持水平方向拖拽返回的Fragment
 * @author yangzc
 */
public class HSlidingBackFragment extends BaseFragment implements
		PanelSlideListener {

	private HSlidingPaneLayout mSlidingPaneLayout;

	private PanelSlideListener mPanelSlideListener;

	private boolean mIsOpened = false;
	
	private boolean mSlideable = false;
	
    protected LayoutInflater mInflater;
    
    private View mShadowView;
	
	@Override
	public final View onCreateViewImpl(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		this.mInflater = inflater;
		if(mSlideable){
//			mShadowView = View.inflate(getActivity(), R.layout.layout_pop_left, null);
			mShadowView = new View(getActivity());
			final LayoutParams layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			View content = onCreateViewImpl(container, savedInstanceState);
			
			this.mSlidingPaneLayout = new HSlidingPaneLayout(getActivity());
			this.mSlidingPaneLayout.setPanelSlideListener(mSlideListener);
			this.mSlidingPaneLayout.setSliderFadeColor(Color.TRANSPARENT);
			this.mSlidingPaneLayout.addView(mShadowView, layoutParams);
			this.mSlidingPaneLayout.addView(content, layoutParams);
			this.mSlidingPaneLayout.setDragView(content);
			if(mDisableTouch){
				this.mSlidingPaneLayout.markClose();
			}
			return mSlidingPaneLayout;
		}else{
			return onCreateViewImpl(container, savedInstanceState);
		}
	}

	public View onCreateViewImpl(ViewGroup container, Bundle savedInstanceState) {
		return null;
	}
	
	public LayoutInflater getLayoutInflater(){
		return mInflater;
	}
	
	@Override
	public void onError(Throwable e) {
//		super.onError(e);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        mShadowView = null;
        mSlidingPaneLayout = null;
		if(mSlidingPaneLayout != null){
			mSlidingPaneLayout.setPanelSlideListener(null);
		}
        mPanelSlideListener = null;
    }

    /**
	 * 设置滚动状态监听器
	 * @param panelSlideListener
	 */
	public void setPanelSlideListener(
			final PanelSlideListener panelSlideListener) {
		this.mPanelSlideListener = panelSlideListener;
	}
	
	private boolean mDisableTouch = false;
	public void setDisableTouch(){
		mDisableTouch = true;
		if(mSlidingPaneLayout != null)
			mSlidingPaneLayout.markClose();
	}
	
	/**
	 * 关闭该页面
	 */
	public void finish() {
		if(mSlidingPaneLayout != null){
			mSlidingPaneLayout.markClose();
			setDragable(true);
			this.mSlidingPaneLayout.openPane();
		}else{
			onPanelClosed(null);
		}
	}
	
	/**
	 * 设置是否可以拖拽
	 * @param dragable
	 */
	public void setDragable(boolean dragable) {
		if(mSlidingPaneLayout != null){
			this.mSlidingPaneLayout.setDragable(dragable);
		}
	}
	
	/**
	 * 是否可以拖拽
	 * @return
	 */
	public boolean isDragable(){
		if(mSlidingPaneLayout != null){
			return this.mSlidingPaneLayout.isDragable();
		}
		return false;
	}
	
	/**
	 * 是否支持滑动
	 * @param slideable
	 */
	public void setSlideable(boolean slideable){
		this.mSlideable = slideable;
	}

    /**
     * 是否为滑动出现
     * @return
     */
    public boolean isSlideable() {
        return mSlideable;
    }

	/**
	 * 是否已经打开这个fragment
	 * @return
	 */
	public boolean isOpened(){
		return mIsOpened;
	}
	
	@Override
	public void onPanelOpened(final View pPanel) {
		if (this.mPanelSlideListener != null) {
			this.mPanelSlideListener.onPanelOpened(pPanel);
		}
		mIsOpened = true;
	}
	
	@Override
	public void onPanelSlide(final View pPanel, final float pSlideOffset) {
		if (this.mPanelSlideListener != null) {
			this.mPanelSlideListener.onPanelSlide(pPanel, pSlideOffset);
		}
		if(mShadowView != null){
			mShadowView.setBackgroundColor(Color.argb((int) (150 *  (1 - pSlideOffset)), 0, 0, 0));
		}
	}

	@Override
	public void onPanelClosed(final View pPanel) {
		if (this.mPanelSlideListener != null) {
			this.mPanelSlideListener.onPanelClosed(pPanel);
		}
		mIsOpened = false;
	}

	private PanelSlideListener mSlideListener = new PanelSlideListener() {
		
		@Override
		public void onPanelSlide(View panel, float slideOffset) {
			HSlidingBackFragment.this.onPanelSlide(panel, slideOffset);
		}
		
		@Override
		public void onPanelOpened(View panel) {
			HSlidingBackFragment.this.onPanelClosed(panel);
		}
		
		@Override
		public void onPanelClosed(View panel) {
			HSlidingBackFragment.this.onPanelOpened(panel);
		}
	};
}
