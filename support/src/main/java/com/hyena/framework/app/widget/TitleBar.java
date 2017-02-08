/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * TitleBar
 * @author yangzc
 */
public abstract class TitleBar extends RelativeLayout {

	private BaseUIFragment<?> mBaseUIFragment;
	//事件监听器
	protected TitleBarListener mListener;
    //更多弹框
//    private PopupWindow mMorePopupWindow;

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitleBar(Context context) {
		super(context);
	}

	public void setBaseUIFragment(BaseUIFragment<?> baseUIFragment){
		this.mBaseUIFragment = baseUIFragment;
	}

	public BaseUIFragment<?> getBaseUIFragment(){
		return mBaseUIFragment;
	}

	/**
	 * 设置标题可见
	 * @param visible
	 */
	public void setTitleVisible(final boolean visible){
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				setVisibility(visible ? View.VISIBLE: View.GONE);
			}
		});
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		
	}
	
	/**
	 * 设置菜单项
	 * @param menuItems
	 */
	public void setMenuItems(List<MenuItem> menuItems){
	}

//	/**
//	 * 设置菜单项
//	 * @param menuItems
//	 */
//	public final void setMenuItems(final List<MenuItem> menuItems) {
//        if (getMoreTextView() == null || getMoreImageView() == null)
//            return;
//
//		if(menuItems != null && !menuItems.isEmpty()){
//			if (menuItems.size() == 1) {
//				if(menuItems.get(0).icon != 0){
//					getMoreImageView().setVisibility(View.VISIBLE);
//					getMoreTextView().setVisibility(View.GONE);
//					getMoreImageView().setImageResource(menuItems.get(0).icon);
//					getMoreImageView().setOnClickListener(new View.OnClickListener(){
//						@Override
//						public void onClick(View v) {
//                            if(mListener != null){
//                                mListener.onMenuSelected(menuItems.get(0));
//                            }
//						}
//					});
//				}else if(!TextUtils.isEmpty(menuItems.get(0).title)){
//					getMoreImageView().setVisibility(View.GONE);
//					getMoreTextView().setVisibility(View.VISIBLE);
//					getMoreTextView().setText(menuItems.get(0).title);
//					getMoreTextView().setOnClickListener(new View.OnClickListener(){
//						@Override
//						public void onClick(View v) {
//                            if(mListener != null){
//                                mListener.onMenuSelected(menuItems.get(0));
//                            }
//						}
//					});
//				} else {
//					getMoreImageView().setVisibility(View.GONE);
//					getMoreTextView().setVisibility(View.GONE);
//				}
//			} else {
//                getMoreImageView().setVisibility(View.VISIBLE);
//                getMoreTextView().setVisibility(View.GONE);
//                getMoreImageView().setImageResource(getDefaultMoreDrawableId());
//                getMoreImageView().setOnClickListener(new View.OnClickListener(){
//                    public void onClick(View v) {
//                        if(mMorePopupWindow != null && mMorePopupWindow.isShowing()){
//                           mMorePopupWindow.dismiss();
//                        }
//                        mMorePopupWindow = showMoreWindow(menuItems, new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                if(position < menuItems.size()){
//                                    if(mListener != null){
//                                        mListener.onMenuSelected(menuItems.get(position));
//                                    }
//                                    if(mMorePopupWindow != null && mMorePopupWindow.isShowing()){
//                                        mMorePopupWindow.dismiss();
//                                    }
//                                }
//                            }
//                        });
//                        int xPos = getResources().getDisplayMetrics().widthPixels / 2
//                                - mMorePopupWindow.getWidth() / 2;
//                        mMorePopupWindow.showAsDropDown(getMoreImageView(), xPos, 0);
//                    }
//                });
//            }
//		} else {
//            getMoreImageView().setVisibility(View.GONE);
//            getMoreTextView().setVisibility(View.GONE);
//        }
//	}

//	/**
//	 * 获得更多的图片按钮
//	 * @return
//	 */
//	public ImageView getMoreImageView(){return null;}
//
//    /**
//     * 获得默认的更多图片
//     * @return
//     */
//    public int getDefaultMoreDrawableId(){
//        return 0;
//    }
//
//	/**
//	 * 获得更多的文本
//	 * @return
//	 */
//	public TextView getMoreTextView(){return null;}
//
//	/**
//	 * 显示更多的弹出框
//	 * @param menuItems
//	 */
//	public PopupWindow showMoreWindow(List<MenuItem> menuItems, AdapterView.OnItemClickListener listener) {
//		return null;
//	}

	/**
	 * 设置标题栏事件监听器
	 * @param listener
	 */
	public void setTitleBarListener(TitleBarListener listener){
		this.mListener = listener;
	}

	/**
	 * 标题栏事件监听器
	 */
	public interface TitleBarListener {

		/**
		 * 后退键点击
		 * @param view
		 */
		void onBackPressed(View view);

		/**
		 * 点击标题
		 * @param view
		 */
		void onTitlePressed(View view);

		/**
		 * 菜单选中
		 * @param menu
		 */
		void onMenuSelected(MenuItem menu);
	}
}
