package com.dd.fakefans.entry;

import com.dd.fakefans.fresco.IFrescoCallBack;
import com.dd.fakefans.fresco.ImageDisplayConfig;
import com.dd.fakefans.fresco.ImageProcessor;

public class Image {
    private String path;
    private ImageProcessor imageProcessor;
    private ImageDisplayConfig imageDisplayConfig;
    // fresco回调
    private IFrescoCallBack iFrescoCallBack;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    public ImageDisplayConfig getImageDisplayConfig() {
        return imageDisplayConfig;
    }

    public void setImageDisplayConfig(ImageDisplayConfig imageDisplayConfig) {
        this.imageDisplayConfig = imageDisplayConfig;
    }

    public IFrescoCallBack getiFrescoCallBack() {
        return iFrescoCallBack;
    }

    public void setiFrescoCallBack(IFrescoCallBack iFrescoCallBack) {
        this.iFrescoCallBack = iFrescoCallBack;
    }
}
