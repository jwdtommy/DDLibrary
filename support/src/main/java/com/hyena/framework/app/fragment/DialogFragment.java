/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * 弹框Fragment
 * 
 * @author yangzc
 *
 * @param <T>
 */
public class DialogFragment<T extends BaseUIFragmentHelper> extends
		AnimationFragment<T> {

	/**
	 * 样式
	 * @author yangzc
	 */
	public static enum AnimStyle {
		STYLE_SCALE
		, STYLE_DROP
		, STYLE_BOTTOM
	}
	
	// 全屏场景View
	protected RelativeLayout mRootView;
	// 内容区域父窗口，为兼容其他特殊需求
	protected RelativeLayout mContentPanelParent;
	// 内容区域View
	protected LinearLayout mContentPanel;
	// 标题TextView
	protected TextView mTitleTxtView;
	// 标题分割线
	protected View mTitleDevider;
	// 按钮控制区域
	protected LinearLayout mButtonPanel;
	// 确认按钮
	protected TextView mConfirmBtn;
	// 取消按钮
	protected TextView mCancelBtn;
	// 控制区域横线分割线
	protected View mCtrlDevider;
	// 按钮之间分割线
	protected View mBtnDevider;

	private View mContentView;
	private String mTitle;
	private String mConfirmTxt;
	private String mCancelTxt;
	private boolean mCanceledOnTouchOutside = true;
	
	private AnimStyle mAnimStyle = AnimStyle.STYLE_SCALE;
	//边框距离
	private int mMargin = 0;
	//对齐方式
	private int mAlign = RelativeLayout.CENTER_IN_PARENT;
	
	//当前dialog层级
	//弹框栈
	static ArrayList<DialogFragment<?>> mDialogStack = new ArrayList<DialogFragment<?>>();
	
	// 是否存在内容冗余父层
	private boolean mHasParentPanel = false;

	{
		/*
		 * 初始化数据快
		 */
		setAnimationType(AnimType.ANIM_NONE);
		setSlideable(false);
		setTitleStyle(STYLE_NO_TITLE);
	}

	/**
	 * 创建弹框Fragment
	 * 
	 * @param activity
	 * @param title
	 * @param contentView
	 * @param confirmTxt
	 * @param cancelTxt
	 * @return
	 */
	public static DialogFragment<?> create(Activity activity, String title,
			View contentView, String confirmTxt, String cancelTxt) {
		DialogFragment<?> fragment = DialogFragment.newFragment(activity,
				DialogFragment.class, null);
		fragment.setAnimationType(AnimType.ANIM_NONE);
		fragment.setSlideable(false);
		fragment.setTitleStyle(STYLE_NO_TITLE);
		fragment.setTitle(title);
		fragment.setContent(contentView);
		fragment.setBtns(confirmTxt, cancelTxt);
		fragment.setCanceledOnTouchOutside(true);
		return fragment;
	}

	/**
	 * 是否存在冗余内容的冗余层
	 * 
	 * @param hasParentPanel
	 */
	public void hasParentPanel(boolean hasParentPanel) {
		this.mHasParentPanel = hasParentPanel;
	}

	/**
	 * 显示当前Fragment
	 */
	public void show() {
		showFragment(this);
		mDialogStack.add(0, this);
		onDialogStackChange();
	}

	/**
	 * 设置内容
	 * 
	 * @param view
	 */
	public void setContent(View view) {
		this.mContentView = view;
	}

	/**
	 * 获得内容View
	 */
	public View getContentView() {
		return mContentView;
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(final String title) {
		this.mTitle = title;
		if (mTitleTxtView == null) {
			return;
		}
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				if (!TextUtils.isEmpty(title)) {
					mTitleTxtView.setText(title);
					mTitleTxtView.setVisibility(View.VISIBLE);
					mTitleDevider.setVisibility(View.VISIBLE);
				} else {
					mTitleTxtView.setVisibility(View.GONE);
					mTitleDevider.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * 获取标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * 设置按钮文案
	 * 
	 * @param confirmTxt
	 * @param cancelTxt
	 */
	public void setBtns(final String confirmTxt, final String cancelTxt) {
		this.mConfirmTxt = confirmTxt;
		this.mCancelTxt = cancelTxt;

		if (!isInited())
			return;

		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				// 刷新按钮状态
				if (TextUtils.isEmpty(mCancelTxt)
						&& TextUtils.isEmpty(mConfirmTxt)) {
					mButtonPanel.setVisibility(View.GONE);
					mCtrlDevider.setVisibility(View.GONE);
				} else if (!TextUtils.isEmpty(mCancelTxt)
						&& !TextUtils.isEmpty(mConfirmTxt)) {
					mButtonPanel.setVisibility(View.VISIBLE);
					mCtrlDevider.setVisibility(View.VISIBLE);
					mBtnDevider.setVisibility(View.VISIBLE);
				} else {
					mButtonPanel.setVisibility(View.VISIBLE);
					mCtrlDevider.setVisibility(View.VISIBLE);
					mBtnDevider.setVisibility(View.GONE);
				}

				// 添加取消按钮
				int windowWidth = UIUtils.getWindowWidth(getActivity())
						- UIUtils.dip2px(getWinsHorizonallMarginDp()) * 2;
				int width = (windowWidth - UIUtils.dip2px(1)) / 2;
				if (TextUtils.isEmpty(mConfirmTxt)
						|| TextUtils.isEmpty(mCancelTxt)) {
					width = width << 1;
				}
				mConfirmBtn.getLayoutParams().width = width;
				mCancelBtn.getLayoutParams().width = width;

				mConfirmBtn.setVisibility(TextUtils.isEmpty(mConfirmTxt) ? View.GONE
						: View.VISIBLE);
				mCancelBtn.setVisibility(TextUtils.isEmpty(mCancelTxt) ? View.GONE
						: View.VISIBLE);
			}
		});
	}

	/**
	 * 关闭窗口
	 */
	public void dismiss() {
		finish();
	}

	/**
	 * 是否点击其他区域退出
	 * 
	 * @param close
	 */
	public void setCanceledOnTouchOutside(boolean close) {
		this.mCanceledOnTouchOutside = close;
		if (mRootView != null) {
			if (close) {
				mRootView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			} else {
				mRootView.setOnClickListener(null);
			}
		}
	}
	
	/**
	 * 是否有动画
	 * @param animStyle
	 */
	public void setAnimStyle(AnimStyle animStyle) {
		this.mAnimStyle = animStyle;
	}

	private int mViewHeight;
	@Override
	public Animator getInAnimator() {
		if (mAnimStyle == AnimStyle.STYLE_SCALE) {
			Animator scaleX = ObjectAnimator.ofFloat(mContentPanel, "scaleX",
					0.0f, 1.0f);
			Animator scaleY = ObjectAnimator.ofFloat(mContentPanel, "scaleY",
					0.0f, 1.0f);
			AnimatorSet animSet = new AnimatorSet();
			animSet.setInterpolator(new DecelerateInterpolator());
			animSet.setDuration(200);
			animSet.playTogether(scaleX, scaleY);
			return animSet;
		} else if(mAnimStyle == AnimStyle.STYLE_DROP) {
			mContentPanel.setVisibility(View.INVISIBLE);
			ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
			animator.setDuration(200);
			animator.setStartDelay(100);
			animator.setInterpolator(new DecelerateInterpolator());
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					int height = UIUtils.getWindowHeight(getActivity());
					float top = (height + mViewHeight) * (Float) animation.getAnimatedValue()/2;
					ViewHelper.setTranslationY(mContentPanel, -top);
				}
			});
			
			animator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					mViewHeight = mContentPanel.getMeasuredHeight();
					mContentPanel.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			return animator;
		} else if(mAnimStyle == AnimStyle.STYLE_BOTTOM) {
			if (getContainerPanel() == null) {
				return null;
			}
			//重置位置
			RelativeLayout.LayoutParams params = (LayoutParams) getContainerPanel().getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			getContainerPanel().setVisibility(View.INVISIBLE);
			
			ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
			animator.setDuration(200);
			animator.setStartDelay(100);
			animator.setInterpolator(new DecelerateInterpolator());
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float bottom = mViewHeight * (Float) animation.getAnimatedValue()/2;
					ViewHelper.setTranslationY(getContainerPanel(), bottom);
				}
			});
			
			animator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					mViewHeight = getContainerPanel().getMeasuredHeight();
					getContainerPanel().setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			return animator;
		} else {
			return null;
		}
	}

	@Override
	public Animator getOutAnimator() {
		if (mAnimStyle == AnimStyle.STYLE_SCALE) {
			Animator scaleX = ObjectAnimator.ofFloat(mContentPanel, "scaleX",
					1.0f, 0.0f);
			Animator scaleY = ObjectAnimator.ofFloat(mContentPanel, "scaleY",
					1.0f, 0.0f);
			AnimatorSet animSet = new AnimatorSet();
			animSet.setInterpolator(new AccelerateInterpolator());
			animSet.setDuration(200);
			animSet.playTogether(scaleX, scaleY);
			return animSet;
		} else if(mAnimStyle == AnimStyle.STYLE_DROP) {
			int screenHeight = UIUtils.getWindowHeight(getActivity());
			Animator animator = ObjectAnimator.ofFloat(mContentPanel,
					"translationY", 0, screenHeight - mContentPanel.getTop());
			animator.setDuration(200);
			animator.setInterpolator(new AccelerateInterpolator());
			return animator;
		} else if(mAnimStyle == AnimStyle.STYLE_BOTTOM) {
			if (getContainerPanel() == null) {
				return null;
			}
			Animator animator = ObjectAnimator.ofFloat(getContainerPanel(),
					"translationY", 0, getContainerPanel().getHeight());
			animator.setDuration(200);
			animator.setInterpolator(new AccelerateInterpolator());
			return animator;
		} else {
			return null;
		}
	}
	
	/**
	 * 获得容器View
	 * @return
	 */
	public ViewGroup getContainerPanel() {
		if (mContentPanelParent != null) {
			return mContentPanelParent;
		}
		return mContentPanel;
	}

	/**
	 * 获得窗口水平MarginDp
	 * 
	 * @return
	 */
	protected int getWinsHorizonallMarginDp() {
		return 35;
	}
	
	/**
	 * 距离距离
	 * @param top
	 */
	public void setMargin(int top) {
		this.mMargin = top;
	}
	
	/**
	 * 对齐方式
	 * @param align
	 */
	public void setAlign(int align) {
		this.mAlign = align;
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		mRootView = new RelativeLayout(getActivity());
		mRootView.setBackgroundColor(0x4c000000);
		// 初始化内容Panel
		mContentPanel = new LinearLayout(getActivity());
		mContentPanel.setOrientation(LinearLayout.VERTICAL);
		mContentPanel.setBackgroundColor(Color.WHITE);
		mContentPanel.setClickable(true);

		int margin = UIUtils.dip2px(getWinsHorizonallMarginDp());
		int width = LinearLayout.LayoutParams.MATCH_PARENT;
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
		RelativeLayout.LayoutParams contentPanelParams = new RelativeLayout.LayoutParams(
				width, height);
		contentPanelParams.addRule(mAlign);
		if (mAlign == RelativeLayout.ALIGN_PARENT_TOP) {
			contentPanelParams.topMargin = mMargin;
		} else if(mAlign == RelativeLayout.ALIGN_PARENT_BOTTOM){
			contentPanelParams.bottomMargin = mMargin;
		}
		contentPanelParams.leftMargin = margin;
		contentPanelParams.rightMargin = margin;

		// 不存在冗余层
		if (!mHasParentPanel) {
			mRootView.addView(mContentPanel, contentPanelParams);
		} else {
			// 初始化并且添加父窗体
			mContentPanelParent = new RelativeLayout(getActivity());
			mRootView.addView(mContentPanelParent, contentPanelParams);
			mContentPanelParent.setClickable(true);

			// 添加内容
			mContentPanelParent.addView(mContentPanel,
					new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT));
		}

		if (mCanceledOnTouchOutside) {
			mRootView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		// ==========================================================
		initAllViews();
		return mRootView;
	}

	@Override
	public void onPanelClosed(View pPanel) {
		super.onPanelClosed(pPanel);
		if (mCancelListener != null) {
			mCancelListener.onCanel(this);
		}
		mDialogStack.remove(this);
		onDialogStackChange();
	}
	
	/**
	 * 弹框堆栈改变
	 */
	private void onDialogStackChange(){
//		if (mDialogStack.isEmpty()) {
//			return;
//		}
//		int index = mDialogStack.indexOf(this);
//		double scale = Math.pow(0.8, index);
//		ViewHelper.setScaleX(getView(), (float) scale);
//		ViewHelper.setScaleY(getView(), (float) scale);
	}

	// 关闭监听器
	private OnCancelListener mCancelListener;

	/**
	 * 设置关闭监听器
	 * 
	 * @param listener
	 */
	public void setOnCancelListener(OnCancelListener listener) {
		this.mCancelListener = listener;
	}

	/**
	 * 获得关闭监听器
	 * 
	 * @return
	 */
	public OnCancelListener getOnCancelListener() {
		return mCancelListener;
	}

	/**
	 * 关闭监听器
	 */
	public static interface OnCancelListener {
		public void onCanel(DialogFragment<?> dialog);
	}

	// 点击动作
	private OnDialogListener mDialogListener;

	public void setOnDialogListener(OnDialogListener listener) {
		this.mDialogListener = listener;
	}

	/**
	 * 按钮点击监听器
	 */
	public static interface OnDialogListener {
		public static final int BUTTON_CONFIRM = 0;
		public static final int BUTTON_CANCEL = 1;

		/**
		 * 按钮点击监听器
		 * 
		 * @param dialog
		 * @param btnId
		 */
		public void onItemClick(DialogFragment<?> dialog, int btnId);
	}

	/**
	 * 初始化所有View
	 */
	protected void initAllViews() {
		// 初始化标题View
		initTitleBar();
		// 添加内容View
		initContent(getContentView());
		// 初始化控制区域
		initCtrlPanel();
	}

	/**
	 * 初始化标题栏
	 */
	protected void initTitleBar() {
		int height = UIUtils.dip2px(45);
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, height);
		titleParams.leftMargin = UIUtils.dip2px(20);
		titleParams.gravity = Gravity.CENTER_VERTICAL;
		mContentPanel.addView(mTitleTxtView = getTitleTxtView(), titleParams);
		if (TextUtils.isEmpty(getTitle())) {
			// 不存在标题则隐藏标题
			mTitleTxtView.setVisibility(View.GONE);
		} else {
			mTitleTxtView.setText(getTitle());
		}
		// 添加分割线
		LinearLayout.LayoutParams deviderParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(0.5f));
		int margin = UIUtils.dip2px(20);
		deviderParams.leftMargin = margin;
		deviderParams.rightMargin = margin;
		if (mContentPanel != null) {
			mContentPanel.addView(mTitleDevider = getDeviderLine(),
					deviderParams);
		}
	}

	/**
	 * 初始化内容View
	 * 
	 * @param contentView
	 */
	protected void initContent(View contentView) {
		if (contentView != null) {
			LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mContentPanel.addView(contentView, contentParams);
		}
	}

	/**
	 * 初始化控制区域
	 */
	protected void initCtrlPanel() {
		// 添加分割线
		LinearLayout.LayoutParams deviderTopParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(0.5f));
		int margin = UIUtils.dip2px(20);
		deviderTopParams.leftMargin = margin;
		deviderTopParams.rightMargin = margin;
		if (mContentPanel != null) {
			mContentPanel.addView(mCtrlDevider = getDeviderLine(),
					deviderTopParams);
		}
		if (TextUtils.isEmpty(mConfirmTxt) && TextUtils.isEmpty(mCancelTxt)) {
			mCtrlDevider.setVisibility(View.GONE);
		}

		// 添加按钮区域
		mButtonPanel = new LinearLayout(getActivity());
		mButtonPanel.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams buttonPanelParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(44));
		mContentPanel.addView(mButtonPanel, buttonPanelParams);

		// 添加取消按钮
		int windowWidth = UIUtils.getWindowWidth(getActivity())
				- UIUtils.dip2px(getWinsHorizonallMarginDp()) * 2;
		int width = (windowWidth - UIUtils.dip2px(1)) / 2;
		if (TextUtils.isEmpty(mConfirmTxt) || TextUtils.isEmpty(mCancelTxt)) {
			width = width << 1;
		}
		LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
				width, LinearLayout.LayoutParams.MATCH_PARENT);
		mButtonPanel.addView(mCancelBtn = getButtonTextView(), cancelParams);
		mButtonPanel.addView(mBtnDevider = getButtonDeviderLine(),
				new LinearLayout.LayoutParams(UIUtils.dip2px(1),
						LinearLayout.LayoutParams.MATCH_PARENT));
		mButtonPanel.addView(mConfirmBtn = getButtonTextView(), cancelParams);

		mConfirmBtn.setVisibility(TextUtils.isEmpty(mConfirmTxt) ? View.GONE
				: View.VISIBLE);
		mCancelBtn.setVisibility(TextUtils.isEmpty(mCancelTxt) ? View.GONE
				: View.VISIBLE);
		if (!TextUtils.isEmpty(mConfirmTxt)) {
			mConfirmBtn.setText(mConfirmTxt);
		}
		if (!TextUtils.isEmpty(mCancelTxt)) {
			mCancelBtn.setText(mCancelTxt);
		}

		if (TextUtils.isEmpty(mConfirmTxt) && TextUtils.isEmpty(mCancelTxt)) {
			mButtonPanel.setVisibility(View.GONE);
		}

		mConfirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDialogListener != null) {
					mDialogListener.onItemClick(DialogFragment.this,
							OnDialogListener.BUTTON_CONFIRM);
				}
			}
		});
		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDialogListener != null) {
					mDialogListener.onItemClick(DialogFragment.this,
							OnDialogListener.BUTTON_CANCEL);
				}
			}
		});
	}

	/**
	 * 获得按钮文本
	 * 
	 * @return
	 */
	protected TextView getButtonTextView() {
		TextView textview = new TextView(getActivity());
		textview.setGravity(Gravity.CENTER);
		textview.setTextColor(0xff8f8f8f);
		textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		return textview;
	}

	/**
	 * 获得分割线
	 */
	protected View getDeviderLine() {
		View view = new View(getActivity());
		view.setBackgroundColor(0xffb8b8b8);
		return view;
	}

	/**
	 * 获得控制区域分割线颜色
	 * 
	 * @return
	 */
	protected View getButtonDeviderLine() {
		View view = new View(getActivity());
		view.setBackgroundColor(0xffd1d1d1);
		return view;
	}

	/**
	 * 获得标题View
	 * 
	 * @return
	 */
	protected TextView getTitleTxtView() {
		TextView textView = new TextView(getActivity());
		textView.setEllipsize(TruncateAt.END);
		textView.setSingleLine(true);
		textView.setTextColor(0xff525252);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		return textView;
	}
	
	@Override
	public void onFriendsDataChange(Intent intent) {
		super.onFriendsDataChange(intent);
		
	}
}
