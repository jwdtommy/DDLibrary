/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.hyena.framework.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.FuzzyKeyMemoryCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

/**
 * 图片获取器
 * @author yangzc
 */
public class ImageFetcher {

	private static ImageFetcher mImageFetcher;
	private DiskCache mDiscCacheAware;
	private MemoryCache mMemoryCache;
	
	private ImageFetcher(){
		initImageFetcher();
	}
	
	/**
	 * 初始化ImageFetcher
	 */
	private void initImageFetcher(){
		File appRoot = FrameworkConfig.getConfig().getAppRootDir();
		File cacheFile = new File(appRoot, "images");
		if (!cacheFile.exists()) {
			cacheFile.mkdirs();
		}
		mDiscCacheAware = new UnlimitedDiskCache(cacheFile, null, new Md5FileNameGenerator());
		
		if (mMemoryCache == null) {
			mMemoryCache = DefaultConfigurationFactory.createMemoryCache(BaseApp.getAppContext(), 0);
		}
		mMemoryCache = new FuzzyKeyMemoryCache(mMemoryCache, MemoryCacheUtils.createFuzzyKeyComparator()){
			@Override
			public boolean put(String key, Bitmap value) {
				try {
					return super.put(key, value);
				} catch (Throwable e) {
				}
				return false;
			}
		};
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(BaseApp.getAppContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCache(mDiscCacheAware)
				.memoryCache(mMemoryCache)
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	public static ImageFetcher getImageFetcher(){
		if(mImageFetcher == null)
			mImageFetcher = new ImageFetcher();
		return mImageFetcher;
	}
	
	/**
	 * 从缓存中获取bitmap
	 * @param uri
	 * @return
	 */
	public Bitmap getBitmapInCache(String uri){
		if(mMemoryCache != null){
			final DisplayMetrics displayMetrics = BaseApp.getAppContext().getResources().getDisplayMetrics();
			ImageSize targetSize = new ImageSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
			String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
			return mMemoryCache.get(memoryCacheKey);
		}
		return null;
	}

	/**
	 * 同步获取图片
     */
	public Bitmap loadImageSync(String url){
		return ImageLoader.getInstance().loadImageSync(url);
	}

	public Bitmap loadImageSync(String url, int width, int height, int rotation){
		return ImageLoader.getInstance().loadImageSync(url, new ImageSize(width, height, rotation));
	}
	
	/**
	 * 显示图片
	 */
	public void loadImage(String url, ImageView imageView, int defaultRes){
		loadImage(url, imageView, defaultRes, null);
	}
	
	public void loadImage(final String url, final ImageView imageView, final int defaultRes, BitmapDisplayer displayer){
		loadImage(url, imageView, defaultRes, displayer, null);
	}

	public void loadImage(final String url, final ImageView imageView, final int defaultRes,
						  BitmapDisplayer displayer, final ImageFetcherListener listener) {
		loadImage(url, imageView, defaultRes, displayer, listener, null);
	}
	
	/**
	 * 显示图片
	 */
	public void loadImage(final String url, final ImageView imageView, final int defaultRes, 
			BitmapDisplayer displayer, final ImageFetcherListener listener, ImageLoadingProgressListener progressListener){
		if(displayer == null)
			displayer = new SimpleBitmapDisplayer();
		ImageLoader.getInstance().cancelDisplayTask(imageView);
		final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(defaultRes)
			.showImageOnFail(defaultRes)
//			.showImageOnLoading(defaultRes)
			.displayer(displayer)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

		try {
			ImageLoader.getInstance().displayImage(url == null ? "": url, imageView, options, listener, progressListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadImage(final String url, final Object tag, final ImageFetcherListener listener){
		loadImage(url, null, tag, listener);
	}

	public void loadImage(final String url, final ImageSize imageSize, final Object tag,
						  final ImageFetcherListener listener) {
		loadImage(url, imageSize, tag, listener, null);
	}
	
	/**
	 * 加载图片
	 */
	public void loadImage(final String url, final ImageSize imageSize, final Object tag,
						  final ImageFetcherListener listener, final ImageLoadingProgressListener progressListener){
		UiThreadHandler.post(new Runnable() {
			
			@Override
			public void run() {
				//sd卡中存在该文件
				DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showImageOnLoading(0)
					.showImageForEmptyUri(0)
					.showImageOnFail(0)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
				
				if(listener != null)
					listener.setTag(tag);
				String imageUrl = url;
				if(imageUrl == null)
					imageUrl= "";
				try{
					ImageLoader.getInstance().loadImage(imageUrl, imageSize, options, listener, progressListener);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 获得缓存路径
	 * @param url
	 * @return
	 */
	public File getCacheFilePath(String url){
		if(mDiscCacheAware != null && mDiscCacheAware.get(url) != null){
			return mDiscCacheAware.get(url);
		}
		return null;
	}
	
	public static abstract class ImageFetcherListener implements ImageLoadingListener {
		private Object mTag;
		public void setTag(Object tag){
			this.mTag = tag;
		}
		public void onLoadStarted(String imageUrl, View view, Object tag){}
		public abstract void onLoadComplete(String imageUrl, Bitmap bitmap, 
				Object object);
		
		
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			onLoadStarted(imageUri, view, mTag);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			File cacheFile = ImageFetcher.getImageFetcher().getCacheFilePath(imageUri);
			if(cacheFile != null && cacheFile.exists()){
				cacheFile.delete();
			}
			onLoadComplete(imageUri, null, mTag);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			onLoadComplete(imageUri, loadedImage, mTag);
			//获取成功图片则通知有新图片获取成功
			if (loadedImage != null)
				getImageFetcher().notifyOnLoadImage(imageUri, loadedImage, mTag);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			File cacheFile = ImageFetcher.getImageFetcher().getCacheFilePath(imageUri);
			if(cacheFile != null && cacheFile.exists()){
				cacheFile.delete();
			}
			onLoadComplete(imageUri, null, mTag);
		}
		
	}

	private List<ImageFetcherListener> mImageFetcherListeners
			= new ArrayList<ImageFetcherListener>();

	public void addImageFetcherListener(ImageFetcherListener listener) {
		if (mImageFetcherListeners.contains(listener))
			return;
		mImageFetcherListeners.add(listener);
	}

	public void removeImageFetcherListener(ImageFetcherListener listener){
		mImageFetcherListeners.remove(listener);
	}

	public void notifyOnLoadImage(String imageUrl, Bitmap bitmap,
								   Object object) {
		if (mImageFetcherListeners != null && !mImageFetcherListeners.isEmpty()) {
			for (int i = 0; i < mImageFetcherListeners.size(); i++) {
				mImageFetcherListeners.get(i).onLoadComplete(imageUrl, bitmap, object);
			}
		}
	}
}
