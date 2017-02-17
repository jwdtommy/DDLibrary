package com.dd.framework.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.framework.utils.UIUtils;

/**
 * Created by J.Tommy on 17/2/14.
 */
public  class CommonTopView extends TopView {
	private TextView mBtnLeft;
	private TextView mBtnRight;
	private TextView mTvTitle;

	public CommonTopView(Context context) {
		super(context);
		init();
	}

	public CommonTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mBtnLeft = new TextView(getContext());
		mBtnLeft.setPadding(UIUtils.dip2px(15),UIUtils.dip2px(15),UIUtils.dip2px(15),UIUtils.dip2px(15));
		mBtnLeft.setText("左按钮");
		RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, UIUtils.dip2px(50));
		paramsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		paramsLeft.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mBtnLeft, paramsLeft);

		mTvTitle = new TextView(getContext());
		mTvTitle.setText("标题");
		RelativeLayout.LayoutParams paramsTitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		paramsTitle.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(mTvTitle, paramsTitle);

		mBtnRight = new TextView(getContext());
		mBtnRight.setText("右按钮");
		mBtnRight.setPadding(UIUtils.dip2px(15),UIUtils.dip2px(15),UIUtils.dip2px(15),UIUtils.dip2px(15));
		RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, UIUtils.dip2px(50));
		paramsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		paramsRight.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mBtnRight, paramsRight);
	}

	public TextView getBtnLeft() {
		return mBtnLeft;
	}

	public TextView getBtnRight() {
		return mBtnRight;
	}

	public TextView getTvTitle() {
		return mTvTitle;
	}
}
