package fakefans.dd.com.fakefans.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class CommonUtils {
    public static final String HALF_HOUR = "half_hour";
    public static final String FIVE_MINUTE = "five_minute";
    public static final String TEN_MINUTE = "ten_minute";
    public static final String ONE_HOUR = "one_hour";
    public static final String SIX_HOUR = "six_hour";
    public static final String TWELVE_HOUR = "twelve_hour";
    public static final String TWO_HOUR = "two_hour";
    public static final String FIVE_DAY = "five_day";
    public static final String TEN_SECOND = "ten_second";

    public static final long INTERVAL_ONE_HOUR = 3600000;// 1小时
    public static final long INTERVAL_HALF_HOUR = 1800000;// 半小时
    public static final long INTERVAL_FIVE_MINUTE = 300000;// 5分钟
    public static final long INTERVAL_TEN_MINUTE = 600000;// 10分钟
    public static final long INTERVAL_TWO_HOUR = 3600000 * 2;// 2小时
    public static final long INTERVAL_SIX_HOUR = 3600000 * 6;// 6小时
    public static final long INTERVAL_TWELVE_HOUR = 3600000 * 12;// 12小时
    public static final long INTERVAL_FIVE_DAY = 3600000 * 24 * 5;// 5天
    public static final long INTERVAL_TEN_SECOND = 1000 * 10;// 10秒钟

    public static final int PAPER_PADDING = 0;

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (Exception e) {

        }
        return apiKey;
    }
    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }




    public static synchronized boolean isNetworkConnected(Context context) {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        isConnected = true;
                    }
                }
            }
        }
        return isConnected;
    }

    public static boolean isUpJELLY_BEAN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }


    public static synchronized boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int networkInfoType = networkInfo.getType();
                if (networkInfoType == ConnectivityManager.TYPE_WIFI || networkInfoType == ConnectivityManager.TYPE_ETHERNET) {
                    return networkInfo.isConnected();
                }
            }
        }
        return false;
    }

    public static boolean isMobileNetworkConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int networkInfoType = networkInfo.getType();
                if (networkInfoType == ConnectivityManager.TYPE_MOBILE) {
                    return networkInfo.isConnected();
                }
            }
        }
        return false;
    }

    public static boolean isAllowOfflineDownload(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_ETHERNET:
            case ConnectivityManager.TYPE_WIFI:
                return true;
            default:
                return false;
        }
    }

    /**
     * 当前应用版本号
     *
     * @return
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (Exception e) {
        }
        return 1;
    }

    /**
     * 获取当前应用版本名称
     *
     * @param context
     * @return
     */
    public static String getCurrentVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (Exception e) {
        }
        return "4.0.0";
    }

    public static void hideKeyBoard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyBoard2(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyBoard(final View view, Context context) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    public static void setFullscreen(Activity activity, boolean on) {
        if (activity != null) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    public static void setFullscreen2(Activity activity, View view, boolean enable) {
        if (enable) {// View.INVISIBLE //SYSTEM_UI_FLAG_FULLSCREEN
            view.setSystemUiVisibility(View.INVISIBLE);
        } else {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        win.setAttributes(winParams);
    }

    public static void setTranslucentNavigation(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        win.setAttributes(winParams);
    }

    // 没有2g 3g判断，手机网络统一为2
    public static int getNetworkType(Context context) {
        int type = 0;
        if (isWifiConnected(context)) {
            type = 1;
        } else if (isMobileNetworkConnected(context)) {
            type = 2;
        }
        return type;
    }




    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
        return date;
    }

    public static String getFormatDate(String date, String format, String parser) {
        try {
            Date d = new SimpleDateFormat(parser).parse(date);
            date = new SimpleDateFormat(format).format(d);
        } catch (Exception e) {
        }
        return date;
    }


    public static boolean isServiceWorked(Context context, Class<?> mClass) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(mClass.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isServiceWorked(Context context, String className) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断时间是否超时
     *
     * @param lastUpdate
     * @return
     */
    public static boolean isTimeOut(long lastUpdate, String flag) {
        if (lastUpdate == 0) {
            return true;
        }
        long time = new Date().getTime();
        long interval = time - lastUpdate;
        if (HALF_HOUR.equals(flag) && interval > INTERVAL_HALF_HOUR) {// 超过半小时
            return true;
        } else if (FIVE_MINUTE.equals(flag) && interval > INTERVAL_FIVE_MINUTE) {// 超过5分钟
            return true;
        } else if (ONE_HOUR.equals(flag) && interval > INTERVAL_ONE_HOUR) {// 超过1小时
            return true;
        } else if (SIX_HOUR.equals(flag) && interval > INTERVAL_SIX_HOUR) {// 超过6小时
            return true;
        } else if (TWELVE_HOUR.equals(flag) && interval > INTERVAL_TWELVE_HOUR) {// 超过12小时
            return true;
        } else if (TWO_HOUR.equals(flag) && interval > INTERVAL_TWO_HOUR) {// 超过2小时
            return true;
        } else if (TEN_MINUTE.equals(flag) && interval > INTERVAL_TEN_MINUTE) {// 超过10分钟
            return true;
        } else if (FIVE_DAY.equals(flag) && interval > INTERVAL_FIVE_DAY) {// 超过5天
            return true;
        } else if (TEN_SECOND.equals(flag) && interval > INTERVAL_TEN_SECOND) {// 超过10秒钟
            return true;
        }
        return false;
    }

    /**
     * 是否是横屏
     * @param context
     * @return
     */
    public static boolean isLand(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        // 横屏
        return Configuration.ORIENTATION_LANDSCAPE == configuration.orientation;
    }

    public static ColorStateList getTextColor(Context context, int res) {
        try {
            ColorStateList colorStateList = ColorStateList.createFromXml(context.getResources(), context.getResources().getXml(res));
            return colorStateList;
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static String getNumberFormat(int number) {
        if (number >= 100000000) {
            return "过亿";
        }
        if (number >= 1000000) {
            return String.valueOf(number / 10000) + "万";
        } else {
            return String.valueOf(number);
        }
    }
}
