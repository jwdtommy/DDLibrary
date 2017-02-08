package com.hyena.framework.animation.nodes;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;

/**
 * 显示文本
 * @author yangzc
 *
 */
public class CTextNode extends CNode {

	private String mText;
	private Paint mPaint;
	private Drawable mBackground;

	protected CTextNode(Director director){
		super(director);
		init();
	}
	
	public static CTextNode create(Director director){
		return new CTextNode(director);
	}
	
	private void init(){
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.RED);
		mPaint.setTextSize(26);
//		setOnNodeClickListener(new OnNodeClickListener() {
//			@Override
//			public void onClick(CNode node) {
//
//			}
//		});
	}
	
	public void setPaint(Paint paint){
		this.mPaint = paint;
	}
	
	public void setBackGround(Drawable drawable){
		this.mBackground = drawable;
	}

	public void setColor(int color){
		this.mColor = color;
		mPaint.setColor(color);
	}

	private int mColor = Color.RED;
	private int mPressedColor = -1;
	public void setPressedColor(int pressedColor){
		this.mPressedColor = pressedColor;
	}

	public void setFontSize(int fontSize){
		mPaint.setTextSize(fontSize);
	}

	@Override
	public boolean onTouch(MotionEvent event) {
		return super.onTouch(event);
	}

	@Override
	protected void onTouchDown() {
		super.onTouchDown();
		if (mPressedColor != -1) {
			mPaint.setColor(mPressedColor);
		}
	}

	@Override
	protected void onTouchUp() {
		super.onTouchUp();
		mPaint.setColor(mColor);
	}

	@Override
	public synchronized void render(Canvas canvas) {
		super.render(canvas);
		if(TextUtils.isEmpty(mText) || mPaint == null)
			return;
		
		if(mBackground != null){
			int x = getPosition().x;
			int y = getPosition().y;
			mBackground.setBounds(x, y, x + getWidth(), y + getHeight());
			mBackground.draw(canvas);
		}

		int x, y;
		switch (mTextAlign) {
			case TOP_LEFT: {
				x = getPosition().x;
				y = getPosition().y + getTextHeight();
				break;
			}
			case TOP_CENTER: {
				x = getPosition().x + (getWidth() - getTextWidth())/2;
				y = getPosition().y + getTextHeight();
				break;
			}
			case TOP_RIGHT: {
				x = getPosition().x + getWidth() - getTextWidth();
				y = getPosition().y + getTextHeight();
				break;
			}
			case CENTER_LEFT: {
				x = getPosition().x;
				Paint.FontMetrics fm = mPaint.getFontMetrics();
				y = (int) (getPosition().y + (getHeight() - fm.bottom - fm.top)/2);
				break;
			}
			case CENTER_CENTER: {
				x = getPosition().x + (getWidth() - getTextWidth())/2;
				Paint.FontMetrics fm = mPaint.getFontMetrics();
				y = (int) (getPosition().y + (getHeight() - fm.bottom - fm.top)/2);
				break;
			}
			case CENTER_RIGHT: {
				x = getPosition().x + getWidth() - getTextWidth();
				Paint.FontMetrics fm = mPaint.getFontMetrics();
				y = (int) (getPosition().y + (getHeight() - fm.bottom - fm.top)/2);
				break;
			}
			case BOTTOM_LEFT: {
				x = getPosition().x;
				y = getPosition().y + getHeight();
				break;
			}
			case BOTTOM_CENTER: {
				x = getPosition().x + (getWidth() - getTextWidth())/2;
				y = getPosition().y + getHeight();
				break;
			}
			case BOTTOM_RIGHT: {
				x = getPosition().x + getWidth() - getTextWidth();
				y = getPosition().y + getHeight();
				break;
			}
			default: {
				x = getPosition().x + (getWidth() - getTextWidth())/2;
				y = getPosition().y + (getHeight() + getTextHeight())/2;
				break;
			}
		}
		canvas.drawText(mText, x, y, mPaint);
	}

	private CAlign mTextAlign = CAlign.CENTER_CENTER;
	public void setTextAlign(CAlign align) {
		this.mTextAlign = align;
	}

	@Override
	public int getHeight(){
		int height = super.getHeight();
		if (height <= 0) {
			height = getTextHeight();
		}
		return height;
	}

	@Override
	public synchronized int getWidth() {
		int width = super.getWidth();
		if (width <= 0) {
			width = getTextWidth();
		}
		return width;
	}

	private int getTextWidth(){
		if (mPaint != null && !TextUtils.isEmpty(mText)) {
			return (int) mPaint.measureText(mText);
		}
		return 0;
	}

	private int getTextHeight(){
		if (mPaint != null) {
			Paint.FontMetrics fm = mPaint.getFontMetrics();
			return (int) (fm.bottom - fm.top);
		}
		return 0;
	}

	public synchronized void setText(String text){
		this.mText = text;
	}

	@Override
	public boolean isValid() {
		return super.isValid();
	}

}
