/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Build;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.util.Log;

/**
 * 崩溃控制帮助类
 * @Author YangZC
 * @Date 2013年9月23日
 * @Version 1.0
 *
 */
public class CrashHelper {

	private static final String CRASH_FILE_NAME = "crash.txt";
	
	/**
	 * 初始化
	 */
	public static void init(){
		final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				try{
					saveCrashLog2File(ex);
				}catch(Throwable e){
				}
				if(defaultHandler != null)
					defaultHandler.uncaughtException(thread, ex);
			}
		});
	}
	
	/**
	 * 获得本地缓存文件
	 * @return
	 */
	private static File getCrashFile(){
		try{
			File parent = android.os.Environment.getExternalStorageDirectory();
			if(!parent.exists())
				parent.mkdirs();
			
			File desc = new File(parent, CRASH_FILE_NAME);
			return desc;
		}catch(Throwable e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存崩溃信息到SD卡中
	 * @param ex
	 */
	public static void saveCrashLog2File(Throwable ex){
		File desc = getCrashFile();
		if(desc == null){
			return;
		}

		if(desc.exists())
			desc.delete();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
		MemoryInfo memoryInfo = new MemoryInfo();
		Debug.getMemoryInfo(memoryInfo);
		
		StringBuffer data = new StringBuffer();
		data.append("ACTION:CRASH");
		data.append("|APPNAME:" + VersionUtils.getAppName(BaseApp.getAppContext()));
		data.append("|V:" + VersionUtils.getVersionName(BaseApp.getAppContext()));
		data.append("|TS:" + sdf.format(new Date()));
		data.append("|DEV:" + Build.MODEL);
		data.append("|SDK:" + Build.VERSION.RELEASE);
		data.append("|BOARD:" + Build.BOARD);
		data.append("|DAVIKM:" + memoryInfo.dalvikSharedDirty);
		data.append("|NATIVEM:" + memoryInfo.nativeSharedDirty);
		data.append("|OTHERM:" + memoryInfo.otherSharedDirty);
		
		//内容
		data.append("|CONTENT:" + Log.getStackTraceString(ex));
		
		OutputStream os = null;
		try {
			os = new FileOutputStream(desc);
			os.write(data.toString().replace("\n", "").getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

