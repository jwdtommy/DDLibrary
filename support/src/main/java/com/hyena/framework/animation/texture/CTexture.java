package com.hyena.framework.animation.texture;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.hyena.framework.animation.Director;

/**
 * 默认纹理
 * 处理图片展现
 * @author yangzc
 */
public class CTexture extends CBaseTexture {

	private Bitmap mBitmap;
	
	protected CTexture(Director director, Bitmap bitmap){
		super(director);
		this.mBitmap = bitmap;
	}
	
	/**
	 * 创建Texture
	 * @param bitmap 纹理
	 * @return
	 */
	public static CTexture create(Director director, Bitmap bitmap){
		CTexture texture = new CTexture(director, bitmap);
		return texture;
	}
	
	@Override
	public void render(Canvas canvas) {
		super.render(canvas);
		if(mBitmap == null || mMatrix == null 
				|| mPaint == null || mBitmap.isRecycled())
			return;

		Point position = getPosition();
		canvas.save();

		if(position == null){
			canvas.translate(0, 0);
		}else{
			canvas.translate(position.x, position.y);
		}
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, mMatrix, mPaint);
		}

		canvas.restore();
	}

	/**
	 * 设置纹理
	 * @param bitmap
	 */
	public void setTexture(Bitmap bitmap){
		this.mBitmap = bitmap;
	}

//	@Override
//	public int getWidth(){
////		if(mBitmap != null){
////			return (int) (mBitmap.getWidth() * mInitScaleX);
////		}
////		return 0;
//		return mWidth;
//	}
//
//	@Override
//	public int getHeight(){
////		if(mBitmap != null){
////			return (int) (mBitmap.getHeight() * mInitScaleY);
////		}
////		return 0;
//		return mHeight;
//	}

//	private int mWidth, mHeight;
//	public void setSize(int width, int height){
//		this.mWidth = width;
//		this.mHeight = height;
//		if (mBitmap != null) {
//			setInitScale((width + 0.0f) / mBitmap.getWidth()
//					, (height + 0.0f)/ mBitmap.getHeight());
//		}
//	}

	@Override
	public void setViewSize(int width, int height) {
		super.setViewSize(width, height);
		if (mBitmap != null) {
			setInitScale((width + 0.0f) / mBitmap.getWidth()
					, (height + 0.0f)/ mBitmap.getHeight());
		}
	}

	/**
	 * 获得纹理图片
	 * @return
	 */
	public Bitmap getTexture(){
		return mBitmap;
	}
}
