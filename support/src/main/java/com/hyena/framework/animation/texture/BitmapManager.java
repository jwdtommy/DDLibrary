package com.hyena.framework.animation.texture;

import java.io.InputStream;
import java.lang.ref.SoftReference;

import com.hyena.framework.animation.utils.BitmapUtils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class BitmapManager extends LruCache<String, Bitmap> {

	private BitmapManager(int maxSize) {
		super(maxSize);
	}
	
	public static BitmapManager create(){
		return new BitmapManager(1024 * 10);
	}
	
	public Bitmap getBitmap(Resources res, int resId){
		Bitmap bitmap = get(resId + "");
		Bitmap value;
		if(bitmap == null|| bitmap.isRecycled()){
			Bitmap newBitmap = BitmapUtils.decodeResourceInternal(res, resId);
			if(newBitmap != null)
				put(resId + "", newBitmap);
			value = newBitmap;
		}else{
			value = bitmap;
		}
		return value;
	}

	public Bitmap getBitmap(String path){
		Bitmap bitmap = get(path);
		Bitmap value;
		if(bitmap == null || bitmap.isRecycled()){
			Bitmap newBitmap = BitmapFactory.decodeFile(path);
			if(newBitmap != null)
				put(path, newBitmap);
			value = newBitmap;
		}else{
			value = bitmap;
		}
		return value;
	}

	public Bitmap getBitmap(String path, InputStream is){
		Bitmap bitmap = get(path);
		Bitmap value;
		if(bitmap == null || bitmap.isRecycled()){
			Bitmap newBitmap = BitmapFactory.decodeStream(is);
			if(newBitmap != null)
				put(path, newBitmap);
			value = newBitmap;
		}else{
			value = bitmap;
		}
		return value;
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key,
								Bitmap oldValue, Bitmap newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		if(value == null || value.isRecycled()){
			return 0;
		}else{
			Bitmap bitmap = value;
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			return width * height * 4;
		}
	}

}
