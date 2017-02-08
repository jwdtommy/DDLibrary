/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.utils;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * 进程控制相关类
 * 
 * @author yangzc
 *
 */
public class ProcessUtils {

	/**
	 * 获得进程名</p>
	 * 
	 * @param context
	 * @return
	 */
	public static String getProcessName(Context context) {
		if (context == null) {
			return "";
		}
		int pid = ProcessUtils.getPid();
		String processName = ProcessUtils.getProcessName(context, pid);
		return processName;
	}

	/**
	 * 获得当前进程的ID</p>
	 * 
	 * @return
	 */
	public static int getPid() {
		int pid = android.os.Process.myPid();
		return pid;
	}

	/**
	 * 获得包名</p>
	 * 
	 * @param context
	 * @param pID
	 * @return
	 */
	public static String getProcessName(Context context, int pID) {
		if (context == null) {
			return "";
		}

		String processName = null;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (am == null) {
			return "";
		}
		List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
		if (l == null || l.isEmpty()) {
			return "";
		}
		Iterator<RunningAppProcessInfo> i = l.iterator();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}

}
