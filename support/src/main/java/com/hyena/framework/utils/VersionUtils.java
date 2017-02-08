package com.hyena.framework.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtils {

	/**
	 * 读取应用版本名
	 * @return
	 */
	public static String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pi == null)
			return null;
		return pi.versionName;
	}
	
	/**
	 * 读取应用版本号
	 * 
	 * @return
	 */
	public static int getVersionCode(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pi == null)
			return -1;
		return pi.versionCode;
	}
	
	/**
	 * 读取应用名称
	 * 
	 * @return
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = null;
			try {
				pi = pm.getPackageInfo(context.getPackageName(), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//检查名称是否合法
			if (pi == null || pi.applicationInfo == null || pi.applicationInfo.name == null)
				return "";
			return pi.applicationInfo.name;
		} catch (Exception e) {
		}
		return "";
	}
}
