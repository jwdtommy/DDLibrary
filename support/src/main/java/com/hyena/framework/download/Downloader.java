/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.download;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.RandomFileHttpListener;


/**
 * 下载核心
 * @author yangzc on 15/8/27.
 */
public class Downloader {

    //超时时间
    private static final int TIME_OUT = 30;
    //开始位置
    private int mStartPos;
    //任务进行状态
    private volatile boolean mCanceled = false;

    //下载任务回调
    private DownloaderListener mDownloadListener;

    /**
     * 设置开始下载位置
     * @param startPos 开始进度
     */
    public void setStartPos(int startPos){
        this.mStartPos = startPos;
    }

    /**
     * 下载监听器
     * @param listener 监听器
     */
    public void setDownloaderListener(DownloaderListener listener){
        this.mDownloadListener = listener;
    }

    /**
     * 终止任务
     */
    public void cancel(){
        mCanceled = true;
    }

    /**
     * 开启下载
     * @param remoteUrl 远程文件URL
     * @param destPath 目标路径
     */
    public boolean startTask(final String remoteUrl, final String destPath) {
        mCanceled = false;
        HttpProvider provider = new HttpProvider();
        HttpResult result = provider.doGet(remoteUrl, TIME_OUT, mStartPos, new RandomFileHttpListener(destPath) {

            private long downloadLen = 0;
            private long contentLength = 0;

            @Override
            public boolean onStart(long startPos, long contentLength) {
                if (mCanceled) {
                    return false;
                }
                //设置开始位置
                downloadLen = startPos;
                this.contentLength = contentLength;
                notifyDownloadStarted(startPos, contentLength);
                return super.onStart(startPos, contentLength);
            }

            @Override
            public boolean onAdvance(byte[] buffer, int offset, int len) {
                if (mCanceled) {
                    return false;
                }
                downloadLen += (len - offset);
                notifyDownloadAdvance(downloadLen, contentLength);
                return super.onAdvance(buffer, offset, len);
            }

            @Override
            public boolean onCompleted() {
                if (mCanceled) {
                    return false;
                }
                return super.onCompleted();
            }

            @Override
            public boolean onReady(String url) {
                if (mCanceled) {
                    return false;
                }
                notifyDownloadReady();
                return super.onReady(url);
            }

            @Override
            public boolean onRelease() {
                if (mCanceled) {
                    return false;
                }
                return super.onRelease();
            }
        }, new KeyValuePair("Connection", "close"));
        //通知任务完成
        if (result.isSuccess()) {
            notifyDownloadSuccess();
		} else {
			notifyDownlaodError(result);
		}
        return result.isSuccess();
    }

    /**
     * 通知准备完成
     */
    private void notifyDownloadReady() {
        if(mDownloadListener != null) {
            mDownloadListener.onDownloadReady(this);
        }
    }

    /**
     * 通知开始启动
     * @param startPos 文件下载的起始位置
     * @param contentLength 文件总长度
     */
    private void notifyDownloadStarted(long startPos, long contentLength) {
        if(mDownloadListener != null) {
            mDownloadListener.onDownloadStarted(this, startPos, contentLength);
        }
    }

    /**
     * 通知下载进行中
     * @param downloadLen 文件已经下载的位置
     * @param contentLength 文件总长度
     */
    private void notifyDownloadAdvance(long downloadLen, long contentLength) {
        if(mDownloadListener != null) {
            mDownloadListener.onDownloadAdvance(this, downloadLen, contentLength);
        }
    }

    /**
     * 通知下载完成
     */
    private void notifyDownloadSuccess() {
        if(mDownloadListener != null) {
            mDownloadListener.onDownloadSuccess(this);
        }
    }
    
    /**
     * 通知下载完成
     * @param result http返回数据
     */
    private void notifyDownlaodError(HttpResult result){
    	 if(mDownloadListener != null) {
             mDownloadListener.onDownloadError(this, result);
         }
    }

    /**
     * 任务下载回调
     */
    static interface DownloaderListener {

        /**
         * 准备完成
         * 文件已经建立
         * @param downloader 下载任务
         */
        void onDownloadReady(Downloader downloader);

        /**
         * 已经开启
         * 连接上服务器 准备开始下载
         * @param downloader 下载任务
         * @param startPos 下载的开始位置
         * @param contentLength 文件的总长度
         */
        void onDownloadStarted(Downloader downloader, long startPos, long contentLength);

        /**
         * 下载进行中
         * 下载进度回调
         * @param downloader 下载任务
         * @param downloadLen 已经下载文件大小
         * @param contentLength 文件的总长度
         */
        void onDownloadAdvance(Downloader downloader, long downloadLen, long contentLength);

        /**
         * 下载成功
         * @param downloader 下载任务
         */
        void onDownloadSuccess(Downloader downloader);
        
        /**
         * 下载失败
         * @param downloader 下载任务
         * @param result http返回数据
         */
        void onDownloadError(Downloader downloader, HttpResult result);
    }
}
