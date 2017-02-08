/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.download.task;

import com.hyena.framework.download.Task;
import com.hyena.framework.download.db.DownloadItem;

public class TaskFactory {

	private static TaskFactory instance;
	
	private TaskFactory(){}
	
	public static TaskFactory getTaskFactory(){
		if (instance == null) {
			instance = new TaskFactory();
		}
		return instance;
	}
	
	public Task buildTask(DownloadItem downloadItem) {
		String sourceType = downloadItem.mSourceType;
		Task task = null;
		if (mDownloadTaskBuilder != null) {
			task = mDownloadTaskBuilder.buildTask(sourceType, downloadItem);
			if (task != null) {
				return task;
			}
		}
		if (UrlTask.SOURCE_TYPE.equals(sourceType)) {
			return UrlTask.createUrlTask(downloadItem);
		}
		return UrlTask.createUrlTask(downloadItem);
	}
	
	private DownloadTaskBuilder mDownloadTaskBuilder;
	public void registDownloadTaskBuilder(DownloadTaskBuilder builder){
		this.mDownloadTaskBuilder = builder;
	}
	
	public static interface DownloadTaskBuilder {
		
		/**
		 * Build download task
		 * @param sourceType type of download source
		 * @param downloadItem downloaditem
		 * @return
		 */
		public Task buildTask(String sourceType, DownloadItem downloadItem);
	}
}
