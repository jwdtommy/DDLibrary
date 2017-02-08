package com.hyena.framework.animation.sprite;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.texture.CTexture;

import android.graphics.Canvas;
import android.graphics.Point;

/**
 * 精灵
 * @author yangzc
 */
public class CSprite extends CActionNode {

	//纹理
	private CTexture mTexture;
	private float mRawScaleX = 1, mRawScaleY = 1;
	
	protected CSprite(Director director, CTexture texture){
		super(director);
		this.mTexture = texture;
		this.mRawScaleX = texture.getScaleX();
		this.mRawScaleY = texture.getScaleY();
	}
	
	/**
	 * 创建精灵
	 * @param texture
	 * @return
	 */
	public static CSprite create(Director director, CTexture texture){
		if(texture == null)
			texture = CTexture.create(director, null);
		CSprite sprite = new CSprite(director, texture);
		return sprite;
	}
	
	/**
	 * 创建精灵
	 * @return
	 */
	public static CSprite create(Director director){
		return create(director, null);
	}
	
	@Override
	public synchronized void update(float dt) {
		super.update(dt);
		if(mTexture != null){
			mTexture.update(dt);
		}
	}
	
	@Override
	public synchronized void render(Canvas canvas) {
		super.render(canvas);

		if (!isValid() || !isVisible()) {
			return;
		}

		if(mTexture == null)
			return;

		Point position = getPosition();
		canvas.save();
		
		if(position == null){
			canvas.translate(0, 0);
		}else{
			canvas.translate(position.x, position.y);
		}
		
		mTexture.render(canvas);
		canvas.restore();
	}
	
	@Override
	public int getWidth() {
		if(mTexture != null)
			return mTexture.getWidth();
		return 0;
	}
	
	@Override
	public int getHeight() {
		if(mTexture != null)
			return mTexture.getHeight();
		return 0;
	}
	
	/**
	 * 设置纹理
	 * @param texture
	 */
	public void setTexture(CTexture texture){
		this.mTexture = texture;
		updateAttr();
	}

	@Override
	protected void onTouchDown() {
		super.onTouchDown();
		if (mTexture != null) {
			mTexture.onTouchDown();
		}
	}

	@Override
	protected void onTouchUp() {
		super.onTouchUp();
		if (mTexture != null) {
			mTexture.onTouchUp();
		}
	}

	/**
	 * 获得纹理
	 * @return
	 */
	public CTexture getTexture(){
		return mTexture;
	}

	private float mDegree;
	@Override
	public void rotate(float degrees) {
		this.mDegree = degrees;
		if(mTexture != null)
			mTexture.rotate(degrees);
	}

	private float mSkewX, mSkewY;
	@Override
	public void setSkew(float skewX, float skewY) {
		this.mSkewX = skewX;
		this.mSkewY = skewY;
		if(mTexture != null)
			mTexture.setSkew(skewX, skewY);
	}

	private float mAnchorX, mAnchorY;
	@Override
	public void setAnchor(float x, float y) {
		this.mAnchorX = x;
		this.mAnchorY = y;
		if(mTexture != null)
			mTexture.setAnchor(x, y);
	}

	private float mSx = 1, mSy = 1;
	@Override
	public void setScale(float sx, float sy) {
		this.mSx = sx;
		this.mSy = sy;
		if(mTexture != null)
			mTexture.setScale(sx * mRawScaleX, sy * mRawScaleY);
	}

	private int mAlpha = 255;
	@Override
	public void setAlpha(int alpha) {
		this.mAlpha = alpha;
		if(mTexture != null)
			mTexture.setAlpha(alpha);
	}

	private void updateAttr() {
		rotate(mDegree);
		setScale(mSx, mSy);
		setAlpha(mAlpha);
		setAnchor(mAnchorX, mAnchorY);
		setSkew(mSkewX, mSkewY);
	}
}
