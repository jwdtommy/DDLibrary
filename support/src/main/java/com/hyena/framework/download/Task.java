/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.download;

import java.util.UUID;

import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.download.Downloader.DownloaderListener;
import com.hyena.framework.download.db.DownloadItem;
import com.hyena.framework.download.db.DownloadTable;
import com.hyena.framework.network.HttpError;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.NetworkProvider;

/**
 * 下载任务基类
 * @author yangzc on 15/8/27.
 */
public abstract class Task implements DownloaderListener, Runnable {

    public static int PRIORITY_LOW = 1;
    public static int PRIORITY_MIDDLE = 2;
    public static int PRIORITY_HIGH = 3;

    public static final int STATUS_UNINITED = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_STARTED = 2;

    public static final int STATUS_PAUSE = 3;
    public static final int STATUS_ADVANCING = 4;

    public static final int STATUS_ERROR = 5;
    public static final int STATUS_COMPLETED = 6;

    //任务ID
    private String mTaskId;
    //开始位置
    private int mStartPos;
    //下载进度
    private int mDownloaded;
    //文件长度
    private int mTotalLen;

    private int mStatus = STATUS_UNINITED;

    //具体的下载任务
    private Downloader mDownload;
    //任务监听器
    private TaskListener mTaskListener;
    //下载表
    private DownloadTable mDownloadTable;

    public Task() {
        //构造一个任务ID
        this(UUID.randomUUID().toString());
    }

    public Task(String taskId){
        this.mTaskId = taskId;
        mDownloadTable = DataBaseManager.getDataBaseManager()
        		.getTable(DownloadTable.class);
        mDownload = new Downloader();
    }
    
    public Task(DownloadItem item){
        this.mTaskId = item.mTaskId;
        this.mStartPos = (int) item.mDownloaded;
        this.mDownloaded = (int) item.mDownloaded;
        this.mTotalLen = (int) item.mTotalLen;
        this.mStatus = item.mStatus;
        LogUtil.v(DownloadManager.TAG, "inited downloaded: " + mDownloaded);
        
        mDownloadTable = DataBaseManager.getDataBaseManager()
        		.getTable(DownloadTable.class);
        mDownload = new Downloader();
    }

    /**
     * 获得远程文件下载路径
     * @return
     */
    public abstract String getRemoteUrl();

    /**
     * 获得本地存储文件路径
     * @return
     */
    public abstract String getDestFilePath();

    /**
     * 获得下载的优先级
     * @return
     */
    public abstract int getPriority();
    
    /**
     * 获得任务类型
     * @return
     */
    public abstract String getTaskType();

    /**
     * 任务监听器
     * @param listener
     */
    public void setTaskListener(TaskListener listener) {
        this.mTaskListener = listener;
    }
    
    /**
     * 获得下载监听器
     * @return
     */
    public TaskListener getTaskListener(){
    	return mTaskListener;
    }

    /**
     * 获得TaskID
     * @return 任务ID
     */
    public String getTaskId(){
        return mTaskId;
    }

    public void setStartPos(int startPos){
        this.mStartPos = startPos;
    }
    
    public int getStartPos(){
        return mStartPos;
    }

    public void setTotalLen(int totalLen){
        this.mTotalLen = totalLen;
    }

    public int getTotalLen() {
        return mTotalLen;
    }

    public int getStatus() {
        return mStatus;
    }
    
    public void setStatus(int status){
    	this.mStatus = status;
    }

    public int getProgress(){
        return mDownloaded;
    }

    /**
     * 下载完成
     */
    public void notifyDownloaded(){
    	if (mTaskListener != null) {
        	mStatus = STATUS_COMPLETED;
			mTaskListener.onComplete(this, TaskListener.REASON_SUCCESS);
		}
    }

    @Override
    public void run() {
        startImpl();
    }

    public void pause() {
    	if (DownloadManager.DEBUG) {
			LogUtil.v(DownloadManager.TAG, "pause taskId: " + mTaskId);
		}
        if (mDownload != null) {
            mDownload.cancel();
        }
    }

    private boolean mValid = true;

    /**
     * 删除不合法的任务
     */
    public void setInvalid() {
        this.mValid = false;
        if (mDownload != null) {
            mDownload.cancel();
        }
    }

    /**
     * 是否合法
     * @return
     */
    public boolean isValid() {
        return mValid;
    }

    /**
     * 开始下载任务
     */
    private void startImpl() {
        if(!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
            if(mTaskListener != null){
            	mStatus = STATUS_ERROR;
                mTaskListener.onComplete(this, TaskListener.REASON_NETWORK);
            }
            return;
        }
        mDownload.setStartPos(getStartPos());
        mDownload.setDownloaderListener(this);
        String remoteUrl = getRemoteUrl();
        String destFilePath = getDestFilePath();
        if(TextUtils.isEmpty(remoteUrl)) {
            if(mTaskListener != null){
            	mStatus = STATUS_ERROR;
                mTaskListener.onComplete(this, TaskListener.REASON_EMPTY_URL);
            }
            return;
        }
        if(TextUtils.isEmpty(destFilePath)) {
            if(mTaskListener != null){
            	mStatus = STATUS_ERROR;
                mTaskListener.onComplete(this, TaskListener.REASON_EMPTY_LOCALPATH);
            }
            return;
        }
        mDownload.startTask(remoteUrl, destFilePath);
    }

    @Override
    public void onDownloadReady(Downloader downloader) {
    	if (DownloadManager.DEBUG) {
			LogUtil.v(DownloadManager.TAG, "onReady taskId: " + mTaskId);
		}
        mStatus = STATUS_READY;
        if (mDownloadTable != null) {
        	mDownloadTable.updateStatus(mTaskId, mStatus);
		}
        if (mTaskListener != null) {
            mTaskListener.onReady(this);
        }
    }

    @Override
    public void onDownloadStarted(Downloader downloader, long startPos, long contentLength) {
    	if (DownloadManager.DEBUG) {
			LogUtil.v(DownloadManager.TAG, "onStarted taskId: " + mTaskId);
		}
        mStatus = STATUS_STARTED;
        if (mDownloadTable != null) {
        	mDownloadTable.updateStatus(mTaskId, mStatus);
        }
        this.mStartPos = (int) startPos;
        this.mDownloaded = (int) startPos;
        LogUtil.v(DownloadManager.TAG, "started downloaded: " + mDownloaded);
        if (mTotalLen == 0) {
        	this.mTotalLen = (int) contentLength;
		}
        
        if (mTaskListener != null) {
            mTaskListener.onStart(this, startPos, mTotalLen);
        }
        if (mDownloadTable != null) {
        	mDownloadTable.updateProgress(mTaskId, startPos, mTotalLen);
		}
    }

    @Override
    public void onDownloadAdvance(Downloader downloader, long downloadLen, long contentLength) {
        if (mDownloadTable != null && mStatus != STATUS_ADVANCING) {
        	mDownloadTable.updateStatus(mTaskId, STATUS_ADVANCING);
        	if (DownloadManager.DEBUG) {
    			LogUtil.v(DownloadManager.TAG, "onAdvance taskId: " + mTaskId);
    		}
        }
        mStatus = STATUS_ADVANCING;
        this.mStartPos = (int) downloadLen;
        this.mDownloaded = (int) downloadLen;

    	if (DownloadManager.DEBUG) {
    		LogUtil.v(DownloadManager.TAG, "advance downloaded: " + mDownloaded);
    	}
//        this.mTotalLen = (int) contentLength;
        
        if (mTaskListener != null) {
            mTaskListener.onProgress(this, downloadLen, mTotalLen);

            if (mDownloadTable != null && isInnerPercentChange()) {
            	mDownloadTable.updateProgress(mTaskId, downloadLen, mTotalLen);
			}
        }
    }
    
    @Override
    public void onDownloadSuccess(Downloader downloader) {
    	if (DownloadManager.DEBUG) {
			LogUtil.v(DownloadManager.TAG, "onComplete taskId: " + mTaskId);
		}
        mStatus = STATUS_COMPLETED;
        if (mDownloadTable != null) {
        	mDownloadTable.updateStatus(mTaskId, mStatus);
        }
    	if (mTaskListener != null) {
    		mTaskListener.onComplete(this, TaskListener.REASON_SUCCESS);
		}
    }

    @Override
    public void onDownloadError(Downloader downloader, HttpResult result) {
        if (mTaskListener != null) {
            int reason = TaskListener.REASON_SUCCESS;
            switch (result.mErrorCode) {
                case HttpError.ERROR_CANCEL_READY:
                case HttpError.ERROR_CANCEL_BEGIN:
                case HttpError.ERROR_CANCEL_ADVANCE:
                case HttpError.ERROR_CANCEL_RESPONSE:
                {
                	mStatus = STATUS_PAUSE;
                    reason = TaskListener.REASON_CANCEL;
                    if (mDownloadTable != null) {
                    	mDownloadTable.updateStatus(mTaskId, mStatus);
                    }
                    if (DownloadManager.DEBUG) {
            			LogUtil.v(DownloadManager.TAG, "onPause taskId: " + mTaskId);
            		}
                    break;
                }
                case HttpError.ERROR_STATUS_CODE:
                case HttpError.ERROR_UNKNOWN:
                case HttpError.ERROR_URL_EMPTY:
                case HttpError.ERROR_NO_AVAILABLE_NETWORK:
                {
                    mStatus = STATUS_ERROR;
                    reason = TaskListener.REASON_NETWORK;

                    if (DownloadManager.DEBUG) {
            			LogUtil.v(DownloadManager.TAG, "onError taskId: " + mTaskId);
            		}
                    if (mDownloadTable != null) {
                    	mDownloadTable.updateStatus(mTaskId, mStatus);
                    }
                    break;
                }
                case HttpError.SUCCESS:
                {
                    reason = TaskListener.REASON_SUCCESS;
                    break;
                }
            }
            mTaskListener.onComplete(this, reason);
        }
    }

    /**
     * 任务监听器
     */
    public static interface TaskListener {

        public static final int REASON_SUCCESS = 0;
        public static final int REASON_NETWORK = 1;
        public static final int REASON_CANCEL = 2;
        public static final int REASON_EMPTY_URL = 3;
        public static final int REASON_EMPTY_LOCALPATH = 4;

        /**
         * 准备完成
         * @param task
         */
        void onReady(Task task);
        
        /**
         * 开始下载
         * @param task 下载任务
         */
        void onStart(Task task, long startPos, long totalLen);
        
        /**
         * 下载进行中
         * @param task 下载任务
         * @param progress
         * @param totalLen
         */
        void onProgress(Task task, long progress, long totalLen);

        /**
         * 下载完成
         * @param task 下载任务
         * @param reason 完成原因
         */
        void onComplete(Task task, int reason);

    }
    
    private int mPercent = -1;
    public boolean isPercentChange(){
    	if (mTotalLen != 0) {
    		int percent = mDownloaded * 100 / mTotalLen;
    		if (percent == mPercent) {
				return false;
			} else {
				mPercent = percent;
				return true;
			}
		}
    	return false;
    }
    
    private int mInnerPercent = -1;
    private boolean isInnerPercentChange(){
    	if (mTotalLen != 0) {
    		int percent = mDownloaded * 100 / mTotalLen;
    		if (percent == mInnerPercent) {
				return false;
			} else {
				mInnerPercent = percent;
				return true;
			}
		}
    	return false;
    }
}
