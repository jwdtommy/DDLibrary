/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.db.DataCacheTable;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.FileUtils;

/**
 * @Author yangzc
 * @Date 2013年9月12日
 * @Version 1.0
 *
 */
public class DebugUtils {

	private static final String TAG = "DebugUtils";
	// 堆栈日志文件名称
	private static final String FILE_NAME = "jenkins_debug.txt";
	// 数据库文件名称
	private static String DB_FILENAME = "knowbox.db";

	/**
	 * 初始化
	 * @param dbFileName
	 */
	public static void init(String dbFileName){
		DB_FILENAME = dbFileName;
	}
	
	/**
	 * 保存当前调用堆栈到文件中
	 * 
	 * @param action
	 */
	public static void saveTrackLog(String action) {
		saveTrackLog(action, new Exception());
	}

	/**
	 * 保存当前调用堆栈到文件中
	 * 
	 * @param action
	 * @param e
	 */
	public static void saveTrackLog(String action, Throwable e) {
		String track = Log.getStackTraceString(e);
		LogUtil.v(TAG, track);

		File file = new File(
				android.os.Environment.getExternalStorageDirectory(), FILE_NAME);
		appendToFile(file, action, track);
	}

	/**
	 * 拷贝数据库到SdCard卡根目录
	 * 
	 * @param context
	 */
	public static void copyDB2Sdcard(Context context) {
		File desc = new File(
				android.os.Environment.getExternalStorageDirectory(),
				DB_FILENAME);
		if (desc.exists())
			desc.delete();
		copyDB2Sdcard(context, desc);
	}

	/**
	 * 拷贝数据库到目标路径
	 * 
	 * @param context
	 * @param desc
	 */
	public static void copyDB2Sdcard(Context context, File desc) {
		File dbFile = context.getDatabasePath(DB_FILENAME);
		try {
			if (desc.exists())
				desc.delete();
			
			FileUtils.copyFile(dbFile, desc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 注册崩溃事件监听 注意：这个方法可能影响统计系统的错误统计数据
	 */
	public static void registCrashListener() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				saveTrackLog(
						"crash at: " + thread.getId() + "-" + thread.getName(),
						ex);
			}
		});
	}

	/**
	 * 输出HProf文件到指定目录
	 * 
	 * @param desc
	 */
	public static void dumpHprofData(File desc) {
		try {
			Debug.dumpHprofData(desc.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试时使用断言来验证程序的正确性
	 * 
	 * @param value
	 */
	@SuppressLint("Assert")
	public static void addAssert(boolean value) {
		if (LogUtil.isDebug()) {
			 assert(value);
		}
	}

	/**
	 * 保存日志到SdCard
	 */
	public static void saveLog2Sdcard() {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("logcat");
			buffer.append(" -d");
			buffer.append(" -f " + getLogFile().getAbsolutePath());
			buffer.append(" -v time");
			Runtime.getRuntime().exec(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存日志到SdCard
	 */
	public static void saveLog2Sdcard(File file) {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("logcat");
			buffer.append(" -d");
			if (file != null)
				buffer.append(" -f " + file.getAbsolutePath());
			else
				buffer.append(" -f " + getLogFile().getAbsolutePath());
			buffer.append(" -v time");
			Runtime.getRuntime().exec(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得日志保存路径
	 * 
	 * @return
	 */
	private static File getLogFile() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmmss",
				Locale.CHINA);
		String data = sdf.format(new Date());
		String fileName = "logcat_" + data + ".log";
		File logFile = new File(
				android.os.Environment.getExternalStorageDirectory(), fileName);
		return logFile;
	}

	/**
	 * 追加字符串到文件末尾
	 * 
	 * @param target
	 * @param str
	 */
	public static void appendToFile(File target, String action, String str) {
		FileOutputStream fos = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CHINA);
			fos = new FileOutputStream(target, true);

			fos.write('\n');
			fos.write((sdf.format(new Date()) + " : " + action).getBytes());
			fos.write('\n');
			fos.write(str.getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 开发方法追查
	 * 
	 * @param traceName
	 */
	public static void startMethodTracing(String traceName) {
		if (LogUtil.isDebug()) {
			Debug.startMethodTracing(traceName);
		}
	}

	/**
	 * 关闭方法追查记录
	 */
	public static void stopMethodTracing() {
		if (LogUtil.isDebug()) {
			Debug.stopMethodTracing();
		}
	}

	private static long mLastTs = -1;

	/**
	 * Debug调试时间
	 * 
	 * @param init
	 * @param init
	 */
	public static void debugCost(boolean init, String tagPrefix) {
		long current = System.currentTimeMillis();
		if (init) {
			mLastTs = current;
		}
		LogUtil.v("DebugTs", tagPrefix + " cost: " + (current - mLastTs));
		mLastTs = current;
	}

	/**
	 * Debug调试时间
	 * 
	 * @param tagPrefix
	 */
	public static void debugCost(String tagPrefix) {
		debugCost(false, tagPrefix);
	}

	/**
	 * 打印Debug数据
	 * @param txt
	 */
	public static void debugTxt(String txt) {
		try {
			DebugService service = (DebugService) BaseApp.getAppContext()
					.getSystemService(DebugService.SERVICE_NAME);
			service.showDebugMsg(txt);
		} catch (Exception e) {
		}
	}

	/**
	 * 清空缓存：包括内存缓存、数据库缓存和本地已缓存文件
	 */
	public static void clearAllCache(){
		DataCacheTable table = DataBaseManager.getDataBaseManager()
				.getTable(DataCacheTable.class);
		table.deleteByCase(null, null);
	}

	public static boolean isDebug() {
		if (LogUtil.isDebug())
			return true;
		return false;
	}
}
