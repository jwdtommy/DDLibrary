/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.hyena.framework.clientlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Process;
import android.util.Log;

/**
 * 提供一个多功能的日志控制功能
 * @Author yangzc
 * @Date 2013年11月4日
 * @Version 1.0
 * 
 */
public class Logger {
	
	//日志各个级别
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int DO_NOT_WRITE_LOG = 7;//不写入日志文件

	private Logger(final String fileName, int level) {
		this.mLogFileName = fileName;
		this.mLevel = level;
	}

	public static Logger getLogger(final String fileName) {
		return getLogger(fileName, DO_NOT_WRITE_LOG);
	}

	private static Map<String, Logger> sLoggers = null;

	public synchronized static Logger getLogger(final String fileName, int level) {

		if (fileName == null || fileName.length() == 0 || level < VERBOSE
				|| level > DO_NOT_WRITE_LOG) {
			throw new IllegalArgumentException(
					"invalid parameter fileName or level");
		}

		Logger logger = null;
		if (sLoggers == null) {
			sLoggers = new HashMap<String, Logger>();
		} else {
			logger = sLoggers.get(fileName);
		}

		if (logger == null) {
			logger = new Logger(fileName, level);
			sLoggers.put(fileName, logger);
		}
		return logger;
	}

	private int mLevel = DO_NOT_WRITE_LOG;
	protected boolean mTrace = true;

	/**
	 * 打印日志到文件
	 */
	protected void traceOff() {
		mTrace = false;
	}
	/**
	 * 打印日志到控制台
	 */
	protected void traceOn() {
		mTrace = true;
	}

	public boolean isTracing() {
		return mTrace;
	}

	public void setLevel(int level) {
		this.mLevel = level;
	}

	public int v(String tag, String msg) {
		return mTrace ? Log.v(tag, msg) : println(VERBOSE, tag, msg);
	}

	public int v(String tag, Throwable tr) {
		return v(tag, getStackTraceString(tr));
	}

	public int d(String tag, String msg) {
		return mTrace ? Log.d(tag, msg) : println(DEBUG, tag, msg);
	}

	public int d(String tag, Throwable tr) {
		return d(tag, getStackTraceString(tr));
	}

	public int w(String tag, String msg) {
		return mTrace ? Log.w(tag, msg) : println(WARN, tag, msg);
	}

	public int w(String tag, Throwable tr) {
		return w(tag, getStackTraceString(tr));
	}

	public int i(String tag, String msg) {
		return mTrace ? Log.i(tag, msg) : println(INFO, tag, msg);
	}

	public int i(String tag, Throwable tr) {
		return i(tag, getStackTraceString(tr));
	}

	public int e(String tag, String msg) {
		return mTrace ? Log.e(tag, msg) : println(ERROR, tag, msg);
	}
	
	public int e(String tag, String msg, Throwable e) {
		return mTrace ? Log.e(tag, msg, e) : println(ERROR, tag, getStackTraceString(e));
	}

	public int e(String tag, Throwable tr) {
		return e(tag, getStackTraceString(tr));
	}

	public String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}

	private final Object mLogLock = new Object();
	private String mLogFileName = null;

	private int println(int priority, String tag, String msg) {
		//保存大于日志级别数据
		if (priority < mLevel) {
			return 0;
		}

		String[] ps = { "", "", "V", "D", "I", "W", "E", "A" };
		SimpleDateFormat df = new SimpleDateFormat("[MM-dd HH:mm:ss.SSS]",
				Locale.CHINESE);
		String time = df.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append(time);
		sb.append("\t");
		sb.append(ps[priority]);
		sb.append("/");
		sb.append(tag);
		int pid = Process.myPid();
		sb.append("(");
		sb.append(pid);
		sb.append("):");
		sb.append(msg);
		sb.append("\n");
		FileWriter writer = null;

		synchronized (mLogLock) {
			try {
				File file = new File(android.os.Environment.getExternalStorageDirectory(), mLogFileName);
				// not exist
				if (!file.exists()) {
					file.createNewFile();
				}
				writer = new FileWriter(file, true);
				writer.write(sb.toString());
			} catch (FileNotFoundException e) {
				return -1;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return 0;
	}
}
