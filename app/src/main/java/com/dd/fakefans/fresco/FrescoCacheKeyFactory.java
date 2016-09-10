package com.dd.fakefans.fresco;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.request.ImageRequest;

public class FrescoCacheKeyFactory extends DefaultCacheKeyFactory {

	private static FrescoCacheKeyFactory sInstance = null;

	protected FrescoCacheKeyFactory() {

	}

	public static synchronized DefaultCacheKeyFactory getInstance() {
		if (sInstance == null) {
			sInstance = new FrescoCacheKeyFactory();
		}

		return sInstance;
	}

	boolean isJpg(String url) {
		boolean isJpg = false;
		if (!TextUtils.isEmpty(url)) {
			url = url.toLowerCase();
			return url.contains(".jpg");
		}
		return isJpg;
	}

	boolean isJpeg(String url) {
		boolean isJpeg = false;
		if (!TextUtils.isEmpty(url)) {
			url = url.toLowerCase();
			return url.contains(".jpeg");
		}
		return isJpeg;
	}

	@Override
	public CacheKey getEncodedCacheKey(ImageRequest request,Object object) {
		if (request.getSourceUri().toString().startsWith("http")) {
			Uri uri = this.getCacheKeySourceUri(request.getSourceUri());
			String url = uri.toString();
			String tmp = url.replace(uri.getHost(), "host");
			if (isJpg(tmp)) {
				tmp = tmp.replace(".jpg", "");
				tmp += ".jpg";
			} else if (isJpeg(tmp)) {
				tmp = tmp.replace(".jpeg", "");
				tmp += ".jpeg";
			}
			return new SimpleCacheKey(tmp);

		} else {
			return super.getEncodedCacheKey(request,"");
		}
	}

	@Override
	public CacheKey getBitmapCacheKey(ImageRequest request,Object object) {
		if (request.getSourceUri().toString().startsWith("http")) {
			Uri uri = this.getCacheKeySourceUri(request.getSourceUri());
			String url = uri.toString();
			String tmp = url.replace(uri.getHost(), "host");
			if (isJpg(tmp)) {
				tmp = tmp.replace(".jpg", "");
				tmp += ".jpg";
			} else if (isJpeg(tmp)) {
				tmp = tmp.replace(".jpeg", "");
				tmp += ".jpeg";
			}
			return new BitmapMemoryCacheKey(tmp, null,
					request.getAutoRotateEnabled(),
					request.getImageDecodeOptions(), (CacheKey) null,
					(String) null,"");
		} else {
			return super.getBitmapCacheKey(request,"");
		}
	}
}
