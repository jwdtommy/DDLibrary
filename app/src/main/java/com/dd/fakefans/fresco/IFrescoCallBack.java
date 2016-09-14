package com.dd.fakefans.fresco;

import android.graphics.Bitmap;

public interface IFrescoCallBack{
    // 处理ImageInfo，返回对应的信息
    void processWithInfo(Bitmap bitmap);

    // 处理图片加载失败的情况
    void processWithFailure();
}