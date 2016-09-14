package com.dd.fakefans.fresco;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.image.ImageInfo;
/*
 * @author adong.
 * 图片展示时的配置
 *  请使用Builder模式构建
 */
public class ImageDisplayConfig {
	//是否自适应大小
	private boolean autoResize;
	// 显示的缩放方式
	private ScaleType mScaleType = ScaleType.CENTER_CROP;
	// 加载失败的图
	private Drawable mFailureDrawable; // if==null（mFailureDrawable=LoadingDrawable）
	// 加载失败的图的缩放方式
	private ScaleType mFailureScaleType = ScaleType.CENTER_CROP;
	// 加载loading的图
	private Drawable mLoadingDrawable;
	// 加载中的图的缩放方式
	private ScaleType mLoadingScaleType = ScaleType.FIT_XY;
	// 圆形
	private boolean mIsCircle = false;// 是否是圆形（例如圆形头像）
	// 圆角弧度
	private int cornerRadius = 0;// 优先考虑此参数是否大于0，再考虑isCircle
	// 图片加载进度条
	private ProgressBarDrawable progressBarDrawable = null;
	// 低分辨率的图片uri
	private Uri lowImageUri = null;
	// 图片质量
	private Priority requestPriority = Priority.MEDIUM;
	//foreground
	private Drawable mForegroundDrawable;

	private ImageDisplayConfig() {

	}

	/**
	 * @author adong
	 */

	public static class ImageDisplayConfigBuilder {
		private ImageDisplayConfig imageDisplayConfig;

		/**
		 * 定义一些基础样式，你可以直接使用，也可以在此基础上进行扩展
		 *
		 * @author adong
		 */
		public enum ImageDisplayStyle {
			STYLE_LIST_COMMON_RADIOUS, // 最普通的列表中正方形带圆角icon样式
			STYLE_LIST_COMMON_NO_RADIOUS, // 最普通的列表中不带圆角icon样式
			STYLE_LIST_COMMON_RADIOUS_DARK, STYLE_MESSAGE_CENTER;// 消息中心
		}

		private ImageDisplayConfigBuilder() {
			this.imageDisplayConfig = new ImageDisplayConfig();
		}

		public static ImageDisplayConfigBuilder newBuilder() {
			return new ImageDisplayConfigBuilder();
		}

		public static ImageDisplayConfigBuilder newBuilder(ImageDisplayStyle style) {
			ImageDisplayConfigBuilder temp = new ImageDisplayConfigBuilder();
			switch (style) {
			case STYLE_LIST_COMMON_RADIOUS:
//				temp.setFailureDrawable(R.drawable.place_holder_icon);// 非.9
//				temp.setLoadingDrawable(R.drawable.place_holder_icon);
//				temp.setFailureScaleType(ScaleType.CENTER_CROP);
//				temp.setLoadingScaleType(ScaleType.CENTER_CROP);
				break;
			case STYLE_LIST_COMMON_NO_RADIOUS:
//				temp.setFailureDrawable(R.drawable.place_holder_pic);// .9
//				temp.setLoadingDrawable(R.drawable.place_holder_pic);
//				temp.setFailureScaleType(ScaleType.FIT_XY);// .9图需要拉伸
//				temp.setLoadingScaleType(ScaleType.FIT_XY);
				break;
			case STYLE_LIST_COMMON_RADIOUS_DARK:
//				temp.setFailureDrawable(R.drawable.place_holder_icon_dark);// 非.9
//				temp.setLoadingDrawable(R.drawable.place_holder_icon_dark);
				temp.setFailureScaleType(ScaleType.CENTER_CROP);
				temp.setLoadingScaleType(ScaleType.CENTER_CROP);
				break;

			case STYLE_MESSAGE_CENTER:
//				temp.setFailureDrawable(R.drawable.message_view_default);//
//				temp.setLoadingDrawable(R.drawable.message_view_default);
//				temp.setFailureScaleType(ScaleType.FIT_XY);//
//				temp.setLoadingScaleType(ScaleType.FIT_XY);
				break;
			// ...
			}
			return temp;
		}

		public ImageDisplayConfig build() {
			return imageDisplayConfig;
		}

		public ImageDisplayConfigBuilder setOnPostImageInfoListener(OnPostProcessImageInfoListener listener) {
			imageDisplayConfig.setOnPostImageInfoListener(listener);
			return this;
		}

		public ImageDisplayConfigBuilder setFailureDrawable(Drawable failure) {
			imageDisplayConfig.setFailureDrawable(failure);
			return this;
		}

		public ImageDisplayConfigBuilder setLoadingDrawable(Drawable loading) {
			imageDisplayConfig.setLoadingDrawable(loading);
			return this;
		}

		public ImageDisplayConfigBuilder setFailureDrawable(Context context, int failure) {
			imageDisplayConfig.setFailureDrawable(context.getResources()
					.getDrawable(failure));
			return this;
		}

		public ImageDisplayConfigBuilder setLoadingDrawable(Context context,int loading) {
			Drawable drawable = context.getResources().getDrawable(loading);
			imageDisplayConfig.setLoadingDrawable(drawable);
			if (imageDisplayConfig.getFailureDrawable() == null) {
				imageDisplayConfig.setFailureDrawable(drawable);
			}
			return this;
		}

		public ImageDisplayConfigBuilder setFailureScaleType(ScaleType failureScaleType) {
			imageDisplayConfig.setFailureScaleType(failureScaleType);
			return this;
		}

		public ImageDisplayConfigBuilder setLoadingScaleType(ScaleType loadingScaleType) {
			imageDisplayConfig.setLoadingScaleType(loadingScaleType);
			return this;
		}

		public ImageDisplayConfigBuilder setScaleType(ScaleType mScaleType) {
			imageDisplayConfig.setScaleType(mScaleType);
			return this;
		}

		public ImageDisplayConfigBuilder setIsCircle(boolean isCircle) {
			imageDisplayConfig.setIsCircle(isCircle);
			return this;
		}
		public ImageDisplayConfigBuilder setAutoResize(boolean autoResize) {
			imageDisplayConfig.setAutoResize(autoResize);
			return this;
		}

		public ImageDisplayConfigBuilder setForeground(Drawable drawable){
			imageDisplayConfig.setForeground(drawable);
			return this;
		}
	}

	public static interface OnPostProcessImageInfoListener {
		public void processImageInfo(ImageInfo imageInfo);
	}

	public OnPostProcessImageInfoListener postProcessImageInfoListener;

	public Drawable getFailureDrawable() {
		return mFailureDrawable;
	}

	public Drawable getLoadingDrawable() {
		return mLoadingDrawable;
	}

	public ScaleType getFailureScaleType() {
		return mFailureScaleType;
	}

	public ScaleType getLoadingScaleType() {
		return mLoadingScaleType;
	}

	public ScaleType getScaleType() {
		return mScaleType;
	}

	public boolean isCircle() {
		return mIsCircle;
	}

	public void setOnPostImageInfoListener(OnPostProcessImageInfoListener listener) {
		postProcessImageInfoListener = listener;
	}

	public void setFailureDrawable(Drawable failure) {
		this.mFailureDrawable = failure;
	}

	public void setLoadingDrawable(Drawable loading) {
		this.mLoadingDrawable = loading;
	}

	public void setFailureScaleType(ScaleType failureScaleType) {
		this.mFailureScaleType = failureScaleType;
	}

	public void setLoadingScaleType(ScaleType loadingScaleType) {
		this.mLoadingScaleType = loadingScaleType;
	}

	public void setScaleType(ScaleType mScaleType) {
		this.mScaleType = mScaleType;
	}

	public void setIsCircle(boolean isCircle) {
		this.mIsCircle = isCircle;
	}

	public int getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	public ProgressBarDrawable getProgressBarDrawable() {
		return progressBarDrawable;
	}

	public void setProgressBarDrawable(ProgressBarDrawable progressBarDrawable) {
		this.progressBarDrawable = progressBarDrawable;
	}

	public Uri getLowImageUri() {
		return lowImageUri;
	}

	public void setLowImageUri(Uri lowImageUri) {
		this.lowImageUri = lowImageUri;
	}

	public Priority getRequestPriority() {
		return requestPriority;
	}

	public void setRequestPriority(Priority requestPriority) {
		this.requestPriority = requestPriority;
	}

	public boolean isAutoResize() {
		return autoResize;
	}

	public void setAutoResize(boolean autoResize) {
		this.autoResize = autoResize;
	}

	public void setForeground(Drawable drawable){
		this.mForegroundDrawable = drawable;
	}

	public Drawable getForeground(){
		return mForegroundDrawable;
	}
}
