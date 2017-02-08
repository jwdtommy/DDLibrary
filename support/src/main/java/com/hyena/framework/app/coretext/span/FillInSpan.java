/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.span;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.hyena.framework.utils.UIUtils;

/**
 * 填空
 * @author yangzc
 */
public abstract class FillInSpan extends BaseSpan {
	
	protected static int FILLIN_WIDTH = UIUtils.dip2px(80);
	protected static int FILLIN_HEIGHT = UIUtils.dip2px(40);
	protected static int MAX_FILLIN_WIDTH = UIUtils.dip2px(120);

	protected static final int PADDING_INNER = UIUtils.dip2px(10);
	protected static final int PADDING_OUTTER = UIUtils.dip2px(10);
	protected static final int PADDING_V_OUTER = UIUtils.dip2px(2);
	
	//显示范围
	protected Rect mRect = new Rect();
	
	public FillInSpan(View attachView){
		super(attachView);
	}
	
	/**
	 * get the wide of content
	 * @return
	 */
	public abstract float getContentWidth();
	
	/**
	 * get input text
	 * @return
	 */
	public abstract String getText();
	
	/**
	 * set input text
	 * @param text
	 */
	public abstract void setText(String text);
	
	/**
	 * 获得宽度
	 * @return
	 */
	public abstract float getWidth();
	
	/**
	 * 获得现实范围
	 * @return
	 */
	@Override
	public Rect getRect() {
		return mRect;
	}
	
	@Override
	public boolean isPositionIn(int x, int y) {
		Rect rect = getRect();
		if (rect != null) {
			return rect.contains(x, y);
		}
		return false;
	}

	/**
	 * 获得上下边框
	 * @param paint
	 * @return
	 */
	protected int[] getBorder(Paint paint){
		Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
		int fontHeight = fmPaint.bottom - fmPaint.top;

		int top = FILLIN_HEIGHT / 2 - fontHeight / 4;
		int bottom = FILLIN_HEIGHT / 2 + fontHeight / 4;
		return new int[]{top, bottom};
	}
	
	private OnRectChangeListener mChangeListener;
	public void setRectChangeListener(OnRectChangeListener listener){
		this.mChangeListener = listener;
	}
	
	/**
	 * notify rect change
	 */
	public void notifyRectChange(){
		if (mChangeListener != null) {
			mChangeListener.onRectChange(getRect());
		}
	}
	
	public static interface OnRectChangeListener{
		
		/**
		 * rect change listener
		 * @param rect
		 */
		public void onRectChange(Rect rect);
	}
}
