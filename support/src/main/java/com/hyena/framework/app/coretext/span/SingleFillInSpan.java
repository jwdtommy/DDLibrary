/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.hyena.framework.utils.UIUtils;


/**
 * 单个空样式
 * @author yangzc
 */
public class SingleFillInSpan extends FillInSpan {

	private static final int ACTION_FLASH = 1;

	private Paint mLinePaint;
	private Paint mBgPaint;
	private Paint mTxtPaint;

	private boolean mFocus = false;
	private boolean mInputHintVisible = false;

	private Handler mHandler;
	//文本内容
	private String mTextStr = "";
	//宽度
	private int mWidth = FILLIN_WIDTH;

	public SingleFillInSpan(View attachView, String txtStr){
		this(attachView, -1, -1, -1, txtStr);
	}
	
	public SingleFillInSpan(View attachView, int defaultWidth, int defaultHeight, int maxWidth, 
			String txtStr) {
		super(attachView);
		this.mTextStr = txtStr;
		
		if (defaultWidth > 0) {
			mWidth = defaultWidth;
		} else {
			mWidth = FILLIN_WIDTH;
		}
		
		if (maxWidth > 0) {
			MAX_FILLIN_WIDTH = maxWidth;
		}
		
		if (defaultHeight > 0) {
			FILLIN_HEIGHT = defaultHeight;
		}
		
		mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBgPaint.setColor(Color.GRAY);
//		mBgPaint.setStyle(Style.STROKE);
		mBgPaint.setStrokeWidth(UIUtils.dip2px(1));

		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStrokeWidth(UIUtils.dip2px(2));

		mTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTxtPaint.setColor(Color.BLACK);
		mTxtPaint.setTextSize(UIUtils.dip2px(26));

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				handleMessageImpl(msg);
			}
		};
	}

	/**
	 * 收到消息
	 * 
	 * @param msg
	 */
	private void handleMessageImpl(Message msg) {
		int what = msg.what;
		switch (what) {
		case ACTION_FLASH: {
			mInputHintVisible = !mInputHintVisible;

			postInvalidate();

			Message next = mHandler.obtainMessage(ACTION_FLASH);
			mHandler.sendMessageDelayed(next, 500);
			break;
		}
		default:
			break;
		}
	}
	
	/**
	 * 设置文本
	 * @param text
	 */
	@Override
	public void setText(String text) {
		this.mTextStr = text;
		requestLayout();
		postInvalidate();
	}
	
	/**
	 * 获得文本
	 * @return
	 */
	@Override
	public String getText() {
		return mTextStr;
	}

	/**
	 * 获得焦点
	 * @param focus
	 */
	@Override
	public void setFocus(boolean focus) {
		this.mFocus = focus;
		if (focus) {
			mHandler.removeMessages(ACTION_FLASH);
			Message next = mHandler.obtainMessage(ACTION_FLASH);
			mHandler.sendMessageDelayed(next, 500);
		} else {
			mHandler.removeMessages(ACTION_FLASH);
			mInputHintVisible = false;
		}
		postInvalidate();
	}
	
	@Override
	public float getWidth() {
		return mWidth;
	}
	
	@Override
	public float getContentWidth() {
		float txtWidth = mTxtPaint.measureText(getText());
		return txtWidth;
	}

	private Rect mTempRect = new Rect();
	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		// 绘制区域
		mTempRect.set((int) x, top, (int) (x + mWidth), bottom);
		if (!mTempRect.equals(mRect)) {
			mRect.set(mTempRect);
			
			notifyRectChange();
			postInvalidate();
			return;
		}
		drawImpl(canvas, mRect);
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end,
			FontMetricsInt fontMetricsInt) {
		if (fontMetricsInt != null) {
			int border[] = getBorder(paint);
			fontMetricsInt.ascent = -border[1];
			fontMetricsInt.top = -border[1];
			fontMetricsInt.bottom = border[0];
			fontMetricsInt.descent = border[0];
		}
		return mWidth;
	}
	
	/**
	 * 绘图实例
	 * @param canvas
	 * @param rect
	 */
	protected void drawImpl(Canvas canvas, Rect rect){
		this.mRect = rect;
		// 计算文字高度
		FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		float textBaseY = FILLIN_HEIGHT - (FILLIN_HEIGHT - fontHeight) / 2
				- fontMetrics.bottom + rect.top;


		// 绘制外边框
		canvas.drawRect(rect.left + PADDING_OUTTER, rect.top
				+ PADDING_V_OUTER, rect.right - PADDING_OUTTER, rect.bottom
				- PADDING_V_OUTER, mBgPaint);

		canvas.save();
		canvas.clipRect(rect.left + PADDING_OUTTER + PADDING_INNER, rect.top
				+ PADDING_V_OUTER,
				rect.right - PADDING_OUTTER - PADDING_INNER, rect.bottom
						- PADDING_V_OUTER);

		// 输入文本
		float txtStrX = 0;
		float txtWidth = 0;
		if (!TextUtils.isEmpty(getText())) {
			txtWidth = mTxtPaint.measureText(getText());
			if (txtWidth + PADDING_INNER * 2 + PADDING_OUTTER * 2 > mRect.width()) {
				txtStrX = mWidth - txtWidth - PADDING_INNER
						- PADDING_OUTTER + rect.left;
			} else {
				txtStrX = (mRect.width() - txtWidth) / 2 + rect.left;
			}
			canvas.drawText(getText(), txtStrX, textBaseY, mTxtPaint);
		} else {
			txtStrX = rect.left + mWidth / 2;
		}

		// 输入指示器
		if (mFocus && mInputHintVisible) {
			float lineHeight = FILLIN_HEIGHT - PADDING_V_OUTER * 2
					- UIUtils.dip2px(10);
			canvas.drawLine(txtStrX + txtWidth, (FILLIN_HEIGHT - lineHeight)
					/ 2 + rect.top, txtStrX + txtWidth,
					(FILLIN_HEIGHT + lineHeight) / 2 + rect.top, mLinePaint);
		}
		canvas.restore();
	}
	
	/**
	 * 重新布局
	 */
	protected void requestLayout(){
		if (!TextUtils.isEmpty(getText())) {
			float txtWidth = getContentWidth();
			float borderWidth = txtWidth + PADDING_INNER *2 + PADDING_OUTTER*2;
			
			if (borderWidth > MAX_FILLIN_WIDTH) {
				mWidth = MAX_FILLIN_WIDTH;
			} else if(borderWidth > FILLIN_WIDTH){
				mWidth = (int) borderWidth;
			} else {
				mWidth = FILLIN_WIDTH;
			}
		}
	}
}
