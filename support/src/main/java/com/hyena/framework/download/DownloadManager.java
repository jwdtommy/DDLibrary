/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.download;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.download.Task.TaskListener;
import com.hyena.framework.download.db.DownloadItem;
import com.hyena.framework.download.db.DownloadTable;
import com.hyena.framework.download.task.TaskFactory;
import com.hyena.framework.security.MD5Util;

/**
 * 下载任务管理器
 * @author yangzc on 15/8/27.
 */
public class DownloadManager {

	public static final String TAG = "DownloadManager";
	public static final boolean DEBUG = false;
	
    private static DownloadManager instance = null;

    private Vector<Task> unFinishedTaskList = new Vector<Task>();
    private Vector<Task> downloadTaskList = new Vector<Task>();
    private Vector<Task> finishedList = new Vector<Task>();
    
    private List<TaskListener> mListeners = null;

    private volatile boolean mIsRunning = false;
    private volatile boolean mCanceled = true;

    private DownloadManager() {
    	reloadTasks();
    }

    /**
     * 获得下载管理器
     * @return
     */
    public static DownloadManager getDownloadManager() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    /**
     * 初始化任务
     */
    public void reloadTasks(){
        DownloadTable table = DataBaseManager.getDataBaseManager().getTable(DownloadTable.class);
        
        //未下载
        List<DownloadItem> items = table.queryUnFinishedDownload();
        unFinishedTaskList.clear();
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                DownloadItem item = items.get(i);
                Task task = TaskFactory.getTaskFactory().buildTask(item);
                task.setStartPos((int) item.mDownloaded);
                task.setTotalLen((int) item.mTotalLen);
                task.setStatus(Task.STATUS_PAUSE);
                File file = new File(item.mDestPath);
                
                if (!file.exists()) {
                	task.setStartPos(0);
                	task.setStatus(Task.STATUS_UNINITED);
                	table.updateProgress(task.getTaskId(), 0, item.mTotalLen);
				}
                table.updateStatus(task.getTaskId(), task.getStatus());
                addTask(task);
            }
        }
        
        //下载完成
        List<DownloadItem> finishItems = table.queryFinishedDownload();
        finishedList.clear();
        if (finishItems != null && !finishItems.isEmpty()) {
            for (int i = 0; i < finishItems.size(); i++) {
                DownloadItem item = finishItems.get(i);
                File file = new File(item.mDestPath);
                if (!file.exists()) {
                	table.removeDownload(item.mTaskId);
					continue;
				}
                Task task = TaskFactory.getTaskFactory().buildTask(item);
                task.setStartPos((int) item.mDownloaded);
                task.setTotalLen((int) item.mTotalLen);
                task.setStatus(Task.STATUS_COMPLETED);
                finishedList.add(task);
            }
        }

        //正在下载
        if (downloadTaskList != null && !downloadTaskList.isEmpty()) {
            for (int i = 0; i < downloadTaskList.size(); i++) {
                Task task = downloadTaskList.get(i);
                task.pause();
            }
        }
        downloadTaskList.clear();
    }
    
    /**
     * 下载URL
     * @param taskId
     * @param srcType;
     * @param url 文件URL
     * @param suggestPath 存储路径
     *
     */
    public Task downloadUrl(String taskId, String srcType, String url, String suggestPath) throws Exception {
    	Task task = getTaskById(taskId);
    	if (task == null) {
    		DownloadTable table = DataBaseManager.getDataBaseManager()
            		.getTable(DownloadTable.class);
        	if (table != null) {
        		DownloadItem item = table.queryDownladByTaskId(taskId);
        		if (item != null) {
        			int status = item.mStatus;
                    task = TaskFactory.getTaskFactory().buildTask(item);
                    task.setStartPos((int) item.mDownloaded);
                    task.setTotalLen((int) item.mTotalLen);
                    if (status != Task.STATUS_COMPLETED) {
                    	task.setStatus(Task.STATUS_PAUSE);
                    	table.updateStatus(task.getTaskId(), task.getStatus());
					}

                    //check file exists
                    File file = new File(task.getDestFilePath());
                    if (!file.exists()) {
                        task.setStartPos(0);
                        task.setStatus(Task.STATUS_UNINITED);
                        table.updateProgress(task.getTaskId(), 0, item.mTotalLen);
                    }

                    if(DEBUG) {
                		LogUtil.v(TAG, "start Download resume, taskId:" + taskId);
                	}
    			} else {
    				item = new DownloadItem();
    				item.mTaskId = taskId;
    				item.mSrcPath = url;
    				item.mDestPath = suggestPath;
    				item.mSourceType = srcType;
    				table.insert(item);
    				
    				task = TaskFactory.getTaskFactory().buildTask(item);
    				if(DEBUG) {
    		    		LogUtil.v(TAG, "start Download new, taskId:" + taskId);
    		    	}
    			}
    		}
		}
    	if (task.getStatus() == Task.STATUS_COMPLETED) {
			return task;
		}

        addTask(task);
        //开启
        mCanceled = false;
        scheduleNext();
        return task;
    }

    public void start(){
        mCanceled = false;
        scheduleNext();
    }

    /**
     * 结束所有任务
     */
    public void cancelAll(){
        mCanceled = true;
        if (downloadTaskList != null && !downloadTaskList.isEmpty()) {
            for (int i = 0; i < downloadTaskList.size(); i++) {
                Task task = downloadTaskList.get(i);
                if (task != null) {
                    task.pause();
                }
            }
            unFinishedTaskList.addAll(0, downloadTaskList);
        }
    }

    public void addTaskListener(TaskListener listener){
        if (mListeners == null)
            mListeners = new ArrayList<TaskListener>();
        mListeners.add(listener);
    }

    public void removeTaskListener(TaskListener listener){
        if (mListeners == null)
            return;
        mListeners.remove(listener);
    }

    public String buildTaskId(String url){
        return MD5Util.encode(url);
    }

    public List<Task> getTaskList(){
        return unFinishedTaskList;
    }

    /**
     * 根据任务ID获得任务
     * @param taskId
     * @return
     */
    public Task getTaskById(String taskId) {
    	for (int i = 0; i < downloadTaskList.size(); i++) {
            if(taskId.equals(downloadTaskList.get(i).getTaskId())){
                return downloadTaskList.get(i);
            }
        }
    	
        for (int i = 0; i < unFinishedTaskList.size(); i++) {
            if(taskId.equals(unFinishedTaskList.get(i).getTaskId())){
                return unFinishedTaskList.get(i);
            }
        }
        
        for (int i = 0; i < finishedList.size(); i++) {
            if(taskId.equals(finishedList.get(i).getTaskId())){
                return finishedList.get(i);
            }
        }
        return null;
    }

    /**
     * 添加任务
     * @param task
     */
    private void addTask(Task task) {
    	if (task == null) {
			return;
		}
        unFinishedTaskList.add(task);
        Collections.sort(unFinishedTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                return rhs.getPriority() - lhs.getPriority();
            }
        });
    }
    
    /**
     * 移除任务
     * @param task
     */
    private void removeTask(Task task) {
        if (task != null)
            task.setInvalid();
    }

    /**
     * 删除任务
     * @param task
     */
    private void clearTask(Task task){
        DownloadTable table = DataBaseManager.getDataBaseManager()
        		.getTable(DownloadTable.class);
    	if (table != null && task != null) {
    		table.removeDownload(task.getTaskId());
		}
        if(task != null) {
            task.setTaskListener(null);
            unFinishedTaskList.remove(task);
        }
    }

    /**
     * 根据任务ID移除任务
     * @param taskId
     */
    public void removeTaskById(String taskId){
        Task task = getTaskById(taskId);
        if(task != null){
            removeTask(task);
        }
    }

    /**
     * 开启下个任务
     */
    private void scheduleNext(){
        if(mIsRunning)
            return;

        mIsRunning = true;
        if(unFinishedTaskList.isEmpty()) {
            mIsRunning = false;
            return;
        }
        Task task = unFinishedTaskList.remove(0);
        if (task != null && downloadTaskList.isEmpty()) {
        	downloadTaskList.add(task);
        	task.setTaskListener(mTaskListener);
        	new Thread(task).start();
		}
    }

    private Task.TaskListener mTaskListener = new Task.TaskListener() {

        @Override
        public void onComplete(Task task, int reason) {
            //check done
            if (task.isValid()) {
                List<Task> delTasks = new ArrayList<Task>();
                for (int i = 0; i < unFinishedTaskList.size(); i++) {
                    Task taskItem = unFinishedTaskList.get(i);
                    if (task.getTaskId().equals(taskItem.getTaskId())) {
                        delTasks.add(taskItem);
                        notifyTaskComplete(taskItem, reason);
                    }
                }
                unFinishedTaskList.removeAll(delTasks);
                downloadTaskList.remove(task);
                //加入到完成队列
                finishedList.add(task);
                finishedList.addAll(delTasks);
                notifyTaskComplete(task, reason);
            } else {
                clearTask(task);
            }
            mIsRunning = false;

            if (mCanceled)
                return;

            scheduleNext();
        }

		@Override
		public void onReady(Task task) {
			notifyTaskReady(task);
		}

		@Override
		public void onStart(Task task, long startPos, long totalLen) {
			notifyTaskStart(task, startPos, totalLen);
		}

		@Override
		public void onProgress(Task task, long progress, long totalLen) {
			notifyTaskProgress(task, progress, totalLen);
		}
    };
    
    private void notifyTaskComplete(Task task, int reason){
    	if (DEBUG) {
			LogUtil.v(TAG, "Task complete, taskId: " + task.getTaskId() + " , complete reason: " + reason);
		}
    	if (mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                TaskListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onComplete(task, reason);
                }
            }
        }
    }
    
    private void notifyTaskProgress(Task task, long progress, long totalLen){
    	if (DEBUG) {
			LogUtil.v(TAG, "Task Progress, taskId: " + task.getTaskId() + " , progress: " + progress + ", totalLen: " + totalLen);
		}
    	if (mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                TaskListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onProgress(task, progress, totalLen);
                }
            }
        }
    }
    
    private void notifyTaskStart(Task task, long startPos, long totalLen){
    	if (DEBUG) {
			LogUtil.v(TAG, "Task started, taskId: " + task.getTaskId() + " , startPos: " + startPos + ", totalLen: " + totalLen);
		}
    	if (mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                TaskListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onStart(task, startPos, totalLen);
                }
            }
        }
    }
    
    private void notifyTaskReady(Task task) {
    	if (DEBUG) {
			LogUtil.v(TAG, "Task ready, taskId: " + task.getTaskId());
		}
    	if (mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                TaskListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onReady(task);
                }
            }
        }
    }
}
