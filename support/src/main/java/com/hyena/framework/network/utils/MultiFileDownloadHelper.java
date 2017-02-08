package com.hyena.framework.network.utils;

import java.io.File;

import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.FileHttpListener;

/**
 * 多文件下载工具类
 * @author yangzc
 *
 */
public class MultiFileDownloadHelper {

	private float mTotalPercent = 0;
	
	public boolean downloadMultiFiles(final MultiHttpListener listener, MultiFile ...files){
		if(files == null)
			return false;
		mTotalPercent = 0;
		HttpProvider provider = new HttpProvider();
		boolean isSuccess = true;
		for(int i=0; i< files.length; i++){
			final MultiFile file = files[i];
			
			HttpResult result = provider.doGet(file.mUrl, 10, new FileHttpListener(file.mFilePath){
				private long mDownloaded = 0;
				private long mContentLength = 1;
				
				@Override
				public boolean onReady(String url) {
					if(getTargetFile().exists())
						getTargetFile().delete();
					return super.onReady(url);
				}
				
				@Override
				public boolean onStart(long startPos, long contentLength) {
					this.mContentLength = contentLength;
					return super.onStart(startPos, contentLength);
				}
				
				@Override
				public boolean onAdvance(byte[] buffer, int offset, int len) {
					mDownloaded += len;
					
					if(listener != null){
						float percent = mTotalPercent + (mDownloaded + 0.0f) * file.mPercent / mContentLength;
						listener.onBuffering(percent);
					}
					
					return super.onAdvance(buffer, offset, len);
				}
				
				@Override
				public boolean onCompleted() {
					mTotalPercent += file.mPercent;
					return super.onCompleted();
				}
			});
			
			if(result != null && result.isSuccess() && result.mContentLength > 0){
				if(new File(file.mFilePath).length() == result.mContentLength){
					if(listener != null)
						listener.onSingleFileComplete(file.mUrl);
				}else{
					if(listener != null){
						listener.onFail();
					}
					isSuccess = false;
					break;
				}
			}else{
				if(listener != null){
					listener.onFail();
				}
				isSuccess = false;
				break;
			}
		}
		return isSuccess;
	}
	
	public static interface MultiHttpListener {
		
		/**
		 * 单个文件下载完成
		 * @param url
		 */
		public void onSingleFileComplete(String url);
		
		/**
		 * 整体下载失败
		 */
		public void onFail();
		
		/**
		 * 整体进度%
		 * @param percent
		 */
		public void onBuffering(float percent);
	}
	
	/**
	 * 多文件下载中单个文件信息
	 * @author yangzc
	 *
	 */
	public static class MultiFile {
		public String mUrl;
		public String mFilePath;
		public float mPercent;
		
		public MultiFile(String url, String filePath, float percent){
			this.mUrl = url;
			this.mFilePath = filePath;
			this.mPercent = percent;
		}
	}
}
