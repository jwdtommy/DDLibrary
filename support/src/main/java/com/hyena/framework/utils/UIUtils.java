package com.hyena.framework.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 跟UI展现相关的通用方法
 * @author yangzc
 *
 */
public class UIUtils {

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	public static int getWindowWidth(Activity activity) {
		if (activity == null)
			return 0;
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @return
	 */
	public static int getWindowHeight(Activity activity) {
		if (activity == null)
			return 0;
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	/**
	 * 获取屏幕信息
	 * @param activity
	 * @return
	 */
	public static DisplayMetrics getWindow(Activity activity) {
		if (activity == null) {
			return null;
		}
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric;
	}
	
	/**
	* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	*/
	public static int dip2px(Context context, float dpValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	*/
	public static int dip2px(float dpValue) {
		WindowManager wm = (WindowManager) BaseApp.getAppContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics(); 
		display.getMetrics(dm);
		float scale = dm.density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	* 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	*/
	public static int px2dip(Context context, float pxValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (pxValue / scale + 0.5f);
	}

	/**
	* 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	*/
	public static int px2dip(float pxValue) {
		WindowManager wm = (WindowManager) BaseApp.getAppContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics(); 
		display.getMetrics(dm);
		float scale = dm.density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 打开输入法面板
	 * @param activity
	 */
	public static void showInputMethod(final Activity activity){
		if(activity == null)return;
		InputMethodManager inputMethodManager = ((InputMethodManager)activity.getSystemService(
				Activity.INPUT_METHOD_SERVICE));
		if(activity.getCurrentFocus() != null){
			inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
		}
	}
	
	/**
	 * 关闭输入法面板
	 * @param activity
	 */
	public static void hideInputMethod(final Activity activity){
		if(activity == null)return;
		InputMethodManager inputMethodManager = ((InputMethodManager)activity.getSystemService(
				Activity.INPUT_METHOD_SERVICE));
		if(activity.getCurrentFocus() != null){
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), 0);
		}
	}
	
	/**
     * 切换软键盘显示状态
     * @param context
     * @param editText
     * @param visible
     */
    public static void setInputMethodVisibility(Context context, EditText editText, boolean visible){
        if (context == null || editText == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (visible) {
            imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            imm.hideSoftInputFromWindow(editText.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        editText.setTag(visible);
    }
	
	/**
	 * 设置当前屏的亮度
	 * @param activity 
	 * @param brightness 亮度值，0-1小数
	 */
	public static void setScreenBrightness(final Activity activity,
			final float brightness) {
		if(activity == null) {
			return;
		}
		final Window window = activity.getWindow();
		window.getAttributes().screenBrightness = brightness;
		window.setAttributes(window.getAttributes());
	}
	
	/**
	 * 获取当前屏的亮度
	 * @param activity 
	 * @return 亮度值
	 */
	public static float getScreenBrightness(final Activity activity) {
		if(activity == null){
			return 1.0f;
		}
		return activity.getWindow().getAttributes().screenBrightness;
	}
	
	/**
	 * 检查当前是否位于HOME界面
	 * @param context
	 * @return 是否位于HOME界面
	 */
	public boolean isLauncherOnTop(Context context) {
		//获得属于桌面的应用的应用包名称
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ArrayList<String> launcherPackageNames = new ArrayList<String>();
		List<ResolveInfo> resolveInfoList = context.getPackageManager()
				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo info : resolveInfoList) {
			launcherPackageNames.add(info.activityInfo.packageName);
		}
		//当前正在运行的包名
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningTaskInfo t : am.getRunningTasks(1)) {
			if (t != null && t.numRunning > 0) {
				ComponentName componentName = t.baseActivity;
				if (componentName != null && launcherPackageNames.contains(componentName.getPackageName()))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否运行在UI线程
	 * @return
	 */
	public static boolean isInUIThread(){
		if(Looper.myLooper() != Looper.getMainLooper()){
			return false;
		}
		return true;
	}

	/**
	 * 通过ID获得View
	 * 
	 * @param activity
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);
	}

	/**
	 * 通过ID获得View
	 * 
	 * @param view
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T findViewById(View view, int id) {
		return (T) view.findViewById(id);
	}

	/**
	 * 震动
	 */
	public static void doVibrator() {
		Vibrator vibrator = (Vibrator) BaseApp.getAppContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}
}
