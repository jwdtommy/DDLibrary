package com.dd.fakefans.fresco;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import com.dd.fakefans.entry.Image;
import com.dd.fakefans.base.App;

/**
 * 使用Fresco加载图片的工具类
 *
 * @author adong
 * @date 2016.7.28
 */
public class FrescoImageLoader {
    public static String TAG = "gamecenter_fresco";
    public static final int FADE_TIME = 120;// 图片显示时淡入动画时长

    public static void initalize(Context context, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context).setBaseDirectoryPath(file)
                .setBaseDirectoryName("v1").setVersion(1).build();
        ImagePipelineConfig.Builder imagePipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig).setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                // 可允许将png进行reSize设置
                .setDecodeMemoryFileEnabled(true);// 是否需要配置新的fetcher?
        configureCaches(imagePipelineConfig, context);
        Fresco.initialize(context, imagePipelineConfig.build());
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {

        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE, // Max entries in the cache
                ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE, // Max length of eviction queue
                Integer.MAX_VALUE); // Max cache entry size

        final MemoryCacheParams encodeBitmapCacheParams = new MemoryCacheParams(ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE, // Max entries in the cache
                ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE, // Max length of eviction queue
                Integer.MAX_VALUE); // Max cache entry size
        configBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        }).setEncodedMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return encodeBitmapCacheParams;
            }
        }).setCacheKeyFactory(new FrescoCacheKeyFactory());
    }

    public static void loadImage(final DraweeView<GenericDraweeHierarchy> draweeView, final Image image,
                                 final ImageDisplayConfig config) {

        if (null == draweeView || image == null || image.getPath() == null) {
            return;
        }

        ImageRequest imageRequest = null;
//        float f= config.getAspectRatio();
//        if (f > 0) {
//            draweeView.setAspectRatio(config.getAspectRatio());
//        }
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(image == null ? ""
                : image.getPath()));
        if (null != config) {
            GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();
            if (null != config.getScaleType()) {
                hierarchy.setActualImageScaleType(config.getScaleType());
            }
            if (config.getFailureDrawable() != null) {
                hierarchy.setFailureImage(config.getFailureDrawable(), config.getFailureScaleType());
            }
            if (null != config.getLoadingDrawable()) {
                hierarchy.setPlaceholderImage(config.getLoadingDrawable(), config.getLoadingScaleType());
            }

            if (null != config.getProgressBarDrawable()) {
                hierarchy.setProgressBarImage(config.getProgressBarDrawable());
            }

            if (null != config.getForeground()) {
                hierarchy.setControllerOverlay(config.getForeground());
            }

            // 圆角
            RoundingParams roundingParams = draweeView.getHierarchy().getRoundingParams();
            if (null == roundingParams) {
                roundingParams = new RoundingParams();
            }
            if (config.getCornerRadius() > 0) {
                roundingParams.setCornersRadii(config.getCornerRadius(), config.getCornerRadius(),
                        config.getCornerRadius(), config.getCornerRadius());
                roundingParams.setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
            } else {
                roundingParams.setCornersRadius(0);
                roundingParams.setRoundAsCircle(config.isCircle());
            }
            hierarchy.setRoundingParams(roundingParams);
        }

        // 后处理器
        if (image != null && image.getImageProcessor() != null) {
            imageRequestBuilder.setPostprocessor(image.getImageProcessor());
        }

        imageRequest = imageRequestBuilder.setProgressiveRenderingEnabled(false)
                .setRequestPriority(config == null ? Priority.HIGH : config.getRequestPriority()).build();

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true);
        if (image.getiFrescoCallBack() != null) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            final DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(
                    imageRequest, TAG);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                public void onNewResultImpl(Bitmap bitmap) {
                    if (dataSource.isFinished() && bitmap != null) {
                        dataSource.close();
                        image.getiFrescoCallBack().processWithInfo(bitmap);
                    } else {
                        image.getiFrescoCallBack().processWithFailure();
                    }
                }
                @Override
                public void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (dataSource != null) {
                        dataSource.close();
                    }
                    image.getiFrescoCallBack().processWithFailure();
                }
            }, CallerThreadExecutor.getInstance());
            // CallerThreadExecutor.getInstance()指该操作会执行在子线程
            // UiThreadImmediateExecutorService 在主线程执行

        }
        if (config.isAutoResize()) {
            builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
                public void onFailure(String id, Throwable throwable) {
                }
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    // TODO Auto-generated method stub
                    updateViewSize(draweeView, imageInfo);
                }
                @Override
                public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                    super.onIntermediateImageSet(id, imageInfo);
                    updateViewSize(draweeView, imageInfo);
                }
            });
        }
        DraweeController draweeController = builder.build();
        draweeView.setController(draweeController);
    }

    private static void updateViewSize(DraweeView<GenericDraweeHierarchy> imageView, ImageInfo imageInfo) {
        if (imageInfo != null && imageInfo.getHeight() > 0) {
            imageView.getLayoutParams().width = imageInfo.getWidth()*6;
            imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageView.setAspectRatio((float) imageInfo.getWidth() / (float)imageInfo.getHeight());
        }
    }

    public static void loadImage(final DraweeView<GenericDraweeHierarchy> draweeView, Image image, int placeHolderRes) {
//        if (placeHolderRes <= 0) {
//            return;
//        }
        ImageDisplayConfig config = ImageDisplayConfig.ImageDisplayConfigBuilder.newBuilder().setFailureDrawable(App.getInstance(), placeHolderRes)
                .setLoadingDrawable(App.getInstance(), placeHolderRes).build();
        loadImage(draweeView, image, config);
    }

    public static void loadImage(final DraweeView<GenericDraweeHierarchy> draweeView, int placeHolderRes) {
        if (placeHolderRes <= 0) {
            return;
        }
        ImageDisplayConfig config = ImageDisplayConfig.ImageDisplayConfigBuilder.newBuilder().setFailureDrawable(App.getInstance(), placeHolderRes)
                .setLoadingDrawable(App.getInstance(), placeHolderRes).build();
        loadImage(draweeView, null, config);
    }

    public static void cleanCache() {
        Fresco.getImagePipeline().clearDiskCaches();
        Fresco.getImagePipeline().clearCaches();
    }

    /**
     * 是否在内存缓存中
     *
     * @param image
     * @return
     */
    public static boolean isInMemoryCache(Image image) {
        if (null == image) {
            return false;
        }
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(image.getPath()));
        // 后处理器
        if (image.getImageProcessor() != null) {
            imageRequestBuilder.setPostprocessor(image.getImageProcessor());
        }

        ImageRequest imageRequest = imageRequestBuilder.setProgressiveRenderingEnabled(false).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        return imagePipeline.isInBitmapMemoryCache(imageRequest);
    }

    public static void preloadImage(Image image, final IFrescoCallBack callback) {
        if (null == image) {
            return;
        }
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(image.getPath()));
        // 后处理器
        if (image.getImageProcessor() != null) {
            imageRequestBuilder.setPostprocessor(image.getImageProcessor());
        }

        ImageRequest imageRequest = imageRequestBuilder.setProgressiveRenderingEnabled(false).build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (callback != null) {
            final DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(
                    imageRequest, TAG);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                public void onNewResultImpl(Bitmap bitmap) {
                    if (dataSource.isFinished() && bitmap != null) {
                        dataSource.close();
                        callback.processWithInfo(bitmap);
                    } else {
                        callback.processWithFailure();
                    }
                }

                @Override
                public void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (dataSource != null) {
                        dataSource.close();
                    }
                    callback.processWithFailure();
                }
            }, CallerThreadExecutor.getInstance());
        }
        imagePipeline.prefetchToDiskCache(imageRequest, TAG);
        imagePipeline.prefetchToBitmapCache(imageRequest, TAG);
    }

    /**
     * @param url
     * @return
     */
    public static File getCacheFileFromFrescoDiskCache(String url) {
        Uri uri = Uri.parse(url);
        ImageRequest request = ImageRequest.fromUri(uri);
        if (null != request) {
            try {
                CacheKey cacheKey = FrescoCacheKeyFactory.getInstance().getEncodedCacheKey(request, "");
                if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                    BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache()
                            .getResource(cacheKey);
                    File cacheFile = ((FileBinaryResource) resource).getFile();
                    return cacheFile;
                } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
                    BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache()
                            .getResource(cacheKey);
                    File cacheFile = ((FileBinaryResource) resource).getFile();
                    return cacheFile;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 从内存中bitmap
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromMomeory(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        ImageRequest imageRequest = null;
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        imageRequest = imageRequestBuilder.setProgressiveRenderingEnabled(false).setRequestPriority(Priority.MEDIUM)
                .build();
        DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline()
                .fetchImageFromBitmapCache(imageRequest, TAG);
        try {
            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
            if (imageReference != null) {
                try {
                    CloseableImage closeableImage = imageReference.get();
                    if (closeableImage instanceof CloseableBitmap) {
                        // do something with the bitmap
                        Bitmap bitmap = (Bitmap) ((CloseableBitmap) closeableImage).getUnderlyingBitmap();
                        return bitmap;
                    }
                } finally {
                    CloseableReference.closeSafely(imageReference);
                }
            }
        } finally {
            dataSource.close();
        }
        return null;
    }

    /**
     * 取bitmap 按照memory->encoded memory->disk->network 的顺序去取数据
     *
     * @param lowestPermittedRequestLevel
     * @async=true 异步获取，async=false 同步获取
     */
    public static void fetchBitmap(Image image, RequestLevel lowestPermittedRequestLevel, boolean async,
                                   final IFrescoCallBack callback) {
        if (image == null || TextUtils.isEmpty(image.getPath())) {
            if (callback != null) {
                callback.processWithFailure();
            }
            return;
        }
        ImageRequest imageRequest = null;
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(image.getPath()));
        imageRequest = imageRequestBuilder.setProgressiveRenderingEnabled(false).setRequestPriority(Priority.MEDIUM)
                .setLowestPermittedRequestLevel(lowestPermittedRequestLevel).build();
        // 后处理器
        if (image != null && image.getImageProcessor() != null) {
            imageRequestBuilder.setPostprocessor(image.getImageProcessor());
        }
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        final DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest,
                TAG);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(Bitmap bitmap) {
                if (dataSource.isFinished() && bitmap != null) {
                    dataSource.close();
                }
                if (callback != null) {
                    if (bitmap != null) {
                        callback.processWithInfo(bitmap);
                    } else {
                        callback.processWithFailure();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (dataSource != null) {
                    dataSource.close();
                }
                if (callback != null) {
                    callback.processWithFailure();
                }
            }
        }, async ? CallerThreadExecutor.getInstance() : UiThreadImmediateExecutorService.getInstance());
        // CallerThreadExecutor.getInstance()指该操作会执行在子线程
        // UiThreadImmediateExecutorService 在主线程执行
    }

}
