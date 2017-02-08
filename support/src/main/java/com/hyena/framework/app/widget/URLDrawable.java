/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.hyena.framework.app.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.widget.TextView;

import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.ImageFetcher.ImageFetcherListener;

/**
 * URLDrawable
 * @author yangzc
 *
 */
public class URLDrawable extends BitmapDrawable {

	private TextView mTextView;
	protected Bitmap mBitmap;
	private String mUrl;

	@SuppressWarnings("deprecation")
	public URLDrawable(TextView textView, int defaultRes, String url) {
		super();
		this.mTextView = textView;
		this.mUrl = url;

		mBitmap = BitmapFactory.decodeResource(textView.getResources(), defaultRes);
	}

	@Override
	public void draw(Canvas canvas) {
		checkCache();
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}

	/**
	 * 检查缓存
	 */
	private void checkCache(){
		Bitmap bitmap = ImageFetcher.getImageFetcher().getBitmapInCache(mUrl);
		if (bitmap != null) {
			this.mBitmap = bitmap;
		}
		
		Rect newRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		Rect rect = getBounds();
		if (rect.equals(newRect)) {
			return;
		} else {
			setBounds(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			mTextView.setText(mTextView.getText());
		}
	}
	
	/**
	 * 从服务器加载图片
	 */
	public void loadFromNet() {
		ImageFetcher.getImageFetcher().loadImage(mUrl, mUrl,
				new ImageFetcherListener() {
					@Override
					public void onLoadComplete(String imageUrl, Bitmap bitmap,
							Object object) {
						if (mTextView == null) {
							return;
						}
						mTextView.setText(mTextView.getText());
					}
				});
	}
}
