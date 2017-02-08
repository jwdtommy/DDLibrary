/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.network.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpResponse;

import android.text.TextUtils;

import com.hyena.framework.network.HttpListener;

public class RandomFileHttpListener implements HttpListener {

	private File mTargetFile = null;
	private RandomAccessFile mRandomAccessFile;
	
	public RandomFileHttpListener(String filePath){
		if(!TextUtils.isEmpty(filePath)){
			File file = new File(filePath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			mTargetFile = file;
		}
	}
	
	public File getTargetFile(){
		return mTargetFile;
	}
	
	@Override
	public boolean onStart(long startPos, long contentLength) {
		if (mRandomAccessFile != null) {
			try {
				mRandomAccessFile.seek(startPos);
				if (startPos == 0) {
					mRandomAccessFile.setLength(contentLength);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onAdvance(byte[] buffer, int offset, int len) {
		if(mRandomAccessFile != null){
			try {
				mRandomAccessFile.write(buffer, offset, len);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onCompleted() {
		return true;
	}

	@Override
	public void onError(int statusCode) {
		onCompleted();
	}

	@Override
	public boolean onReady(String url) {
		try {
			mRandomAccessFile = new RandomAccessFile(mTargetFile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean onResponse(InputStream is, OutputStream os, int statusCode
			, String mimeType, String encoding, long contentLength
			, boolean repeatable, boolean isTrunk) {
		return false;
	}

	@Override
	public boolean onRelease() {
		try {
			if(mRandomAccessFile != null)
				mRandomAccessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
