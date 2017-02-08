package com.hyena.framework.network.utils;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.network.HttpExecutor.OutputStreamHandler;
import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.DataHttpListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 文件上传
 * @author yangzc
 */
public class SimpleFileUploader {

	public String doUploadFile(final String url, final File file, final UploadListener listener){
		if(!file.exists()){
			return null;
		}
		
		HttpProvider httpProvider = new HttpProvider();
		
		final long fileLength = file.length();
		if(fileLength <= 0){
			return null;
		}
		
		final String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
		
		HttpResult result = httpProvider.doPost(url, new OutputStreamHandler(){

			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					byte buffer[] = new byte[1024];
					int len = -1;
					long sendLen = 0;
					
					byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
					StringBuilder sb = new StringBuilder();
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Type:application/octet-stream\r\n\r\n");
					byte[] data = sb.toString().getBytes();
					os.write(data);
					
					while((len = fis.read(buffer, 0, 1024)) != -1){
						os.write(buffer, 0, len);
						sendLen += len;
						if(listener != null){
							listener.onProgress(sendLen, fileLength);
						}
					}
					os.write(end_data);
					
					os.flush();
				} finally {
					if(fis != null)
						fis.close();
				}
			}

			@Override
			public long getLength() {
				return fileLength;
			}
			
		}, new DataHttpListener(){
			@Override
			public boolean onReady(String url) {
				if(listener != null){
					listener.onStartUpload();
				}
				return super.onReady(url);
			}
			
			@Override
			public void onError(int statusCode) {
				super.onError(statusCode);
				
				if(listener != null){
					listener.onError(statusCode);
				}
			}
			
			@Override
			public boolean onRelease() {
				boolean result = super.onRelease();
				
				if(listener != null){
					listener.onUploadSuccess();
				}
				
				return result;
			}
		}, 
		new KeyValuePair("connection", "Keep-Alive"),
		new KeyValuePair("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"),
		new KeyValuePair("connection", "Keep-Alive"),
		new KeyValuePair("Charsert", "UTF-8"),
		new KeyValuePair("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
		);
		
		if(result != null && result.isSuccess()){
			return result.getResult();
		}
		
		return null;
	}
	
	public static interface UploadListener {
		public void onStartUpload();
		public void onProgress(long sendLen, long length);
		public void onUploadSuccess();
		
		public void onError(int statusCode);
	}
}
