package com.hyena.framework.network.utils;

import java.io.File;

import com.hyena.framework.network.utils.SimpleFileUploader.UploadListener;

/**
 * 上传管理器
 * @author yangzc
 */
public class UploaderManager {

	private static UploaderManager _instance = null;
	
	private UploaderManager(){}
	
	public static UploaderManager getInstance(){
		if(_instance == null)
			_instance = new UploaderManager();
		return _instance;
	}
	
	//先简单处理上传逻辑
	public void addUploadTask(final String url, final File file, final UploadListener listener){
		new Thread(){
			public void run() {
				SimpleFileUploader uploader = new SimpleFileUploader();
				uploader.doUploadFile(url, file, listener);
			};
		}.start();
	}
}
