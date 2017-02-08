/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.hyena.framework.app.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.animation.utils.UIUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.app.widget.CircleHintView;
import com.hyena.framework.app.widget.TitleBar;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * 通用标题Bar
 * @author yangzc
 */
public class CommonTitleBar extends TitleBar {

	protected TextView mTitleTxt;
	protected TextView mSubTitleTxt;
	protected ImageButton mBackBtn;
    
	protected ImageView mMenuImageBtn;
	protected TextView mMenuTxtBtn;
	protected CircleHintView mTipView;
	protected RelativeLayout mMenuListPanel;
	
	public CommonTitleBar(Context context) {
		super(context);
		//初始化View元素
		init();
	}

	public CommonTitleBar(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		//初始化View元素
		init();
	}
	
	/**
	 * 初始化View元素
	 */
	protected void init(){
		//初始化标题
		initTitleBar();
		//初始化后退键
		initBackBtn();
		//初始化菜单Bar
		initMenuBar();
		//初始化提示
		initTips();
	}

	/**
	 * 初始化后退键
	 */
	@SuppressWarnings("deprecation")
	private void initBackBtn(){
		ImageButton backBtn = new ImageButton(getContext());
		int padding = UIUtils.dp2px(getContext(), 15);
		backBtn.setPadding(padding, padding, padding, padding);
		backBtn.setBackgroundDrawable(null);

		int size = UIUtils.dp2px(getContext(), 50);
		RelativeLayout.LayoutParams backBtnParams = new RelativeLayout
				.LayoutParams(size, size);
		backBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mBackBtn = backBtn, backBtnParams);
		
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListener != null)
                    mListener.onBackPressed(v);
			}
		});
	}
	
	/**
	 * 初始化TitleBar
	 */
	private void initTitleBar(){
		//初始化标题
		LinearLayout titlePanel = new LinearLayout(getContext());
		titlePanel.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleParams.gravity = Gravity.CENTER_HORIZONTAL;
		titlePanel.addView(mTitleTxt = getTitleTextView(), titleParams);
		
		//初始化副标题
		LinearLayout.LayoutParams subTitleParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		subTitleParams.gravity = Gravity.CENTER_HORIZONTAL;
		subTitleParams.topMargin = UIUtils.dp2px(getContext(), 10);
		titlePanel.addView(mSubTitleTxt = getSubTitleTextView(), subTitleParams);
		mSubTitleTxt.setVisibility(View.GONE);
		
		//添加标题栏
		RelativeLayout.LayoutParams titlePanelParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		titlePanelParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(titlePanel, titlePanelParams);
	}
	
	/**
	 * 初始化菜单Bar
	 */
	private void initMenuBar(){
		LinearLayout menuPanel = new LinearLayout(getContext());
		menuPanel.setOrientation(LinearLayout.HORIZONTAL);
		RelativeLayout.LayoutParams menuParams = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		menuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(menuPanel, menuParams);
		
		//图片形式
		int size = UIUtils.dp2px(getContext(), 50);
		LinearLayout.LayoutParams barImageParams = new LinearLayout.LayoutParams(size, size);
		barImageParams.gravity = Gravity.CENTER_VERTICAL;
		menuPanel.addView(mMenuImageBtn = getMenuBtn(), barImageParams);
		mMenuImageBtn.setVisibility(View.GONE);
		
		//文本形式
		LinearLayout.LayoutParams barTxtParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		barTxtParams.gravity = Gravity.CENTER_VERTICAL;
		menuPanel.addView(mMenuTxtBtn = getMenuTextView(), barTxtParams);
		mMenuTxtBtn.setVisibility(View.GONE);
	}
	
	/**
	 * 初始化提示
	 */
	private void initTips(){
		//提示
		CircleHintView tipView = getTipVew();
		int size = UIUtils.dp2px(getContext(), 16);
		RelativeLayout.LayoutParams barTipsParams = new RelativeLayout.LayoutParams(
				size, size);
		barTipsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		int margin = UIUtils.dp2px(getContext(), 5);
		barTipsParams.rightMargin = margin;
		barTipsParams.topMargin = margin;
		addView(mTipView = tipView, barTipsParams);
		mTipView.setVisibility(View.GONE);
	}

	/**
	 * 获得提示红点
	 * @return
	 */
	private CircleHintView getTipVew() {
		CircleHintView view = new CircleHintView(getContext());
		return view;
	}
	
	/**
	 * 初始化菜单按钮
	 */
	private ImageView getMenuBtn(){
		ImageView menuBtn = new ImageView(getContext());
		int padding = UIUtils.dp2px(getContext(), 15);
		menuBtn.setPadding(padding, padding, padding, padding);
		return menuBtn;
	}
	
	/**
	 * 获得菜单文本
	 * @return
	 */
	private TextView getMenuTextView(){
		TextView textView = new TextView(getContext());
		textView.setEllipsize(TruncateAt.END);
		textView.setSingleLine(true);
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		int padding = UIUtils.dp2px(getContext(), 15);
		textView.setPadding(padding, padding, padding, padding);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		return textView;
	}
	
	/**
	 * 获得标题TextView
	 * @return
	 */
	protected TextView getTitleTextView(){
		TextView textView = new TextView(getContext());
		textView.setEllipsize(TruncateAt.END);
		textView.setSingleLine(true);
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER);
		textView.setMaxWidth(UIUtils.dp2px(getContext(), 200));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		return textView;
	}
	
	/**
	 * 获得子标题TextView
	 * @return
	 */
	protected TextView getSubTitleTextView(){
		TextView textView = new TextView(getContext());
		textView.setEllipsize(TruncateAt.MIDDLE);
		textView.setSingleLine(true);
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER);
		textView.setMaxWidth(UIUtils.dp2px(getContext(), 200));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		return textView;
	}
	
	@Override
	public void setTitle(final String title) {
		super.setTitle(title);
		if (mTitleTxt != null) {
			UiThreadHandler.post(new Runnable(){
				@Override
				public void run() {
					setTitleVisible(true);
					mTitleTxt.setText(title);
				}
			});
		}
	}
	
	/**
	 * 设置二级标题
	 * @param title
	 */
	public void setSubTitle(final String title){
		if (mSubTitleTxt != null) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					setTitleVisible(true);
					mSubTitleTxt.setVisibility(View.VISIBLE);
					mSubTitleTxt.setText(title);
				}
			});
		}
	}
	
	public void setTitleColor(final int color) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if (mTitleTxt != null) {
                    mTitleTxt.setTextColor(color);
                }
            }
        });
    }
	
	/**
     * 设置返回键是否可见
     * @param visible
     */
    public void setBackBtnVisible(final boolean visible) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });
    }
    
    /**
     * 设置后退键图片
     * @param resId
     */
    public void setBackBtnResource(final int resId) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mBackBtn.setImageResource(resId);
            }
        });
    }
    
    /**
     * 设置标题背景颜色
     * @param color
     */
    public void setTitleBgColor(final int color){
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setBackgroundColor(color);
            }
        });
    }
    
    /**
     * 设置右侧图片是否可用
     * @param isEnable
     */
    public void setTitleMoreEnable(final boolean isEnable) {
    	UiThreadHandler.post(new Runnable() {
    		@Override
    		public void run() {
    			setVisibility(View.VISIBLE);
    			if(mMenuImageBtn != null)
    				mMenuImageBtn.setEnabled(isEnable);
    			if(mMenuTxtBtn != null)
    				mMenuTxtBtn.setEnabled(isEnable);
    		}
    	});
	}
    
    /**
     * 右侧点击动作
     * @param txt
     * @param clickListener
     */
    public void setMenuMoreTxt(final String txt, final OnClickListener clickListener) {
    	UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(txt)){
                    mMenuTxtBtn.setVisibility(View.GONE);
                    return;
                }
                mMenuImageBtn.setVisibility(View.GONE);
                mMenuTxtBtn.setVisibility(View.VISIBLE);
                mMenuTxtBtn.setText(txt);
                mMenuTxtBtn.setOnClickListener(clickListener);
            }
        });
    }
    
    /**
     * 更多文本
     * @param txt
     * @param clickListener
     */
    public void setMenuMoreTxt(final String txt) {
    	UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(txt)){
                    mMenuTxtBtn.setVisibility(View.GONE);
                    return;
                }
                mMenuImageBtn.setVisibility(View.GONE);
                mMenuTxtBtn.setVisibility(View.VISIBLE);
                mMenuTxtBtn.setText(txt);
            }
        });
    }
    
    /**
     * 右侧点击动作
     * @param txt
     * @param clickListener
     */
    public void setMenuMoreImage(final int resId, final OnClickListener clickListener) {
    	UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mMenuImageBtn.setVisibility(View.VISIBLE);
                mMenuImageBtn.setImageResource(resId);
                mMenuImageBtn.setOnClickListener(clickListener);
                mMenuTxtBtn.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * 显示提示红点
     * @param tips
     */
    public void showTips(final String tips){
    	UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				mTipView.setVisibility(View.VISIBLE);
				mTipView.setText(tips);
			}
		});
    }
    
    /**
     * 隐藏Tips
     */
    public void hiddenTips(){
    	UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				mTipView.setVisibility(View.GONE);
			}
		});
    }
    
	@Override
	public void setMenuItems(final List<MenuItem> menuItems) {
		super.setMenuItems(menuItems);
		if (menuItems != null && !menuItems.isEmpty()) {
			setSingleMenu(menuItems.get(0));
			if (menuItems.size() > 1) {
				//刷掉菜单点击事件
				mMenuImageBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMenuList(menuItems);
					}
				});
			} 
		}
	}
	
	/**
	 * 显示MenuList
	 * @param menuList
	 */
	protected void showMenuList(List<MenuItem> menuList){
		mMenuListPanel = new RelativeLayout(getContext());
		BaseUIFragment<?> fragment = getBaseUIFragment();
		if (fragment != null && fragment.getRootView() != null) {
			
			fragment.getRootView().addView(mMenuListPanel, new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 
					RelativeLayout.LayoutParams.MATCH_PARENT));
			
			//创建MenuListView
			View menulistPanel = createMenuListView(menuList);
			if (menulistPanel != null) {
				//添加MaskView
				View maskView = new View(getContext());
				maskView.setBackgroundColor(Color.parseColor("#4c000000"));
				mMenuListPanel.addView(maskView, new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, 
						RelativeLayout.LayoutParams.MATCH_PARENT));
				maskView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismissMenuListPanel();
					}
				});
				
				//添加MenuListPanel
				int width = UIUtils.dp2px(getContext(), 180);
				RelativeLayout.LayoutParams menuListPanelParams = new RelativeLayout.LayoutParams(
						width, RelativeLayout.LayoutParams.WRAP_CONTENT);
				menuListPanelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				menuListPanelParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mMenuListPanel.addView(menulistPanel, menuListPanelParams);
			}
		}
	}
	
	/**
	 * 关闭菜单Menu
	 */
	public void dismissMenuListPanel() {
		if (mMenuListPanel == null) {
			return;
		}
		BaseUIFragment<?> fragment = getBaseUIFragment();
		if (fragment != null && fragment.getRootView() != null){
			fragment.getRootView().removeView(mMenuListPanel);
		}
	}
	
	/**
	 * 生成MenuListView
	 * @return
	 */
	public View createMenuListView(List<MenuItem> menuList){
		return null;
	}
	
	/**
	 * 显示单个Menu
	 * @param menuItem
	 */
	private void setSingleMenu(final MenuItem menuItem){
		if (menuItem.icon != 0) {
			mMenuImageBtn.setVisibility(View.VISIBLE);
			mMenuTxtBtn.setVisibility(View.GONE);
			mMenuImageBtn.setImageResource(menuItem.icon);
			mMenuImageBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mListener != null){
                        mListener.onMenuSelected(menuItem);
                    }
				}
			});
		} else if(!TextUtils.isEmpty(menuItem.title)) {
			mMenuImageBtn.setVisibility(View.GONE);
			mMenuTxtBtn.setVisibility(View.VISIBLE);
			mMenuTxtBtn.setText(menuItem.title);
			mMenuTxtBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mListener != null){
                        mListener.onMenuSelected(menuItem);
                    }
				}
			});
		} else {
			mMenuImageBtn.setVisibility(View.GONE);
			mMenuTxtBtn.setVisibility(View.GONE);
		}
	}
}
