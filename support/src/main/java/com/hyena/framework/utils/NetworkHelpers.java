package com.hyena.framework.utils;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.telephony.TelephonyManager;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.network.utils.HttpUtils;
import com.hyena.framework.utils.BaseApp;

/**
 * 网络工具类
 * 
 * @version 1.0
 * @date 2011-1-19
 */
@SuppressWarnings("deprecation")
public class NetworkHelpers {

	public static final int CONNECT_NONE = 0;
	public static final int CONNECT_WIFI=2;
	public static final int CONNECT_MOBILE=1;
	public static final int CONNECT_2G = 3;
	public static final int CONNECT_3G = 4;
	public static final int CONNECT_4G = 5;
	
	public static final String WAP = "wap";
	public static final String NET = "net";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	public static final String PROXY_IP = "10.0.0.172";

	private static final String TAG = "NetworkHelpers"; 
	private static boolean mIsCMWAP;
	
	/**
	 * Returns whether the network is available
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context == null)
			return false;
		try{
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				LogUtil.d(TAG, "+++couldn't get connectivity manager");
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							LogUtil.d(TAG, "+++network is available");
							return true;
						}
					}
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}

		LogUtil.d(TAG, "+++network is not available");

		return false;
	}

	/**
	 * Returns whether the network is roaming
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			LogUtil.d(TAG, "+++couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
				if (tm.isNetworkRoaming()) {
					LogUtil.d(TAG, "+++network is roaming");
					return true;
				} else {
					LogUtil.d(TAG, "+++network is not roaming");
				}
			} else {
				LogUtil.d(TAG, "+++not using mobile network");
			}
		}
		return false;
	}

	public static boolean isNetworkCMWAP() {

		return mIsCMWAP;
	}

	public static void checkCMWAP(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			LogUtil.d(TAG, "+++couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				mIsCMWAP = false;
				return;
			}
			LogUtil.d(TAG, "+++ info.getTypeName(): *** " + info.getTypeName());
			LogUtil.d(TAG, "+++ info.getSubtypeName(): *** " + info.getSubtypeName());
			LogUtil.d(TAG, "+++ info.getExtraInfo(): *** " + info.getExtraInfo());
			if ("cmwap".equals(info.getExtraInfo())) {
				mIsCMWAP = true;
			} else {
				mIsCMWAP = false;
			}
		}
	}

	public static boolean isUsingMobileNetwork(Context context){
		try{
			ConnectivityManager connec = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connec.getActiveNetworkInfo();
			String typeName = "";
			if (info != null) {
				typeName = info.getTypeName();
			} else {
				return false;
			}
			
			if ("mobile".equalsIgnoreCase(typeName)) {
				return true;
			} else {
				return false;
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isUsingWifiNetwork(Context context){
		
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager == null)
			return false;
		 NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		 if(mWifi == null)
			 return false;
		 return mWifi.isConnected();
	}
	
	/**
	 * 判断网络, 对外提供服务
	 * @param context
	 * @return
	 */
	public static int getConnectType(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connec == null){
			return CONNECT_NONE;
		}
		NetworkInfo info = connec.getActiveNetworkInfo();
		String typeName = "";
		int net = CONNECT_NONE;
		if (info != null) {
			typeName = info.getTypeName();
		} else {
			return net;
		}

		if ("mobile".equalsIgnoreCase(typeName)) {
			net = getMobileConnectType(context);
		} else if ("wifi".equalsIgnoreCase(typeName)) {
			net = CONNECT_WIFI;
		}
		return net;
	}
	
	/**
	 * 判断移动手机网络
	 * @param context
	 * @return
	 */
	public static int getMobileConnectType(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int subType = tm.getNetworkType();
		switch (subType) {
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case 12: // TelephonyManager.NETWORK_TYPE_EVDO_B
		case 13: // TelephonyManager.NETWORK_TYPE_LTE:
		case 14: // TelephonyManager.NETWORK_TYPE_EHRPD
		case 15: // TelephonyManager.NETWORK_TYPE_HSPA
			return CONNECT_3G;

		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return CONNECT_2G;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
		default:
			return CONNECT_MOBILE;
		}
	}

	public static int getNetworkCarrier(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getSimOperator();
		
		if(operator != null){ 
			if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
				//中国移动
				return 0;
			} else if(operator.equals("46001")) {
				//中国联通
				return 1;
			} else if(operator.equals("46003")) {
				//中国电信
				return 2;
			} 	
		}
		
		return 3;
	}
	
	/**
	 * 获取当前用户的imsi
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;
	}
	
	
	public static boolean isUniomSim(Context context){
//    	TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//    	String imsi = telManager.getSubscriberId();  
//    	if(!TextUtil.isEmpty(imsi)){
//    		 if (imsi.startsWith("46001"))
//    			 return true;
//    	}
//    	return false;
		return getNetworkCarrier(context) == 1;
	}
	
	private static WifiLock wifiLock;
	
	public static void takeWifiLock()
    {
        LogUtil.d("Taking wifi lock");
        if (wifiLock == null){
            WifiManager manager = (WifiManager) BaseApp.getAppContext().getSystemService(Context.WIFI_SERVICE);
            wifiLock = manager.createWifiLock("SwiFTP");
            wifiLock.setReferenceCounted(false);
        }
        wifiLock.acquire();
    }

	public static void releaseWifiLock(){
		LogUtil.d("Releasing wifi lock");
        if (wifiLock != null){
            wifiLock.release();
            wifiLock = null;
        }
    }

	public static boolean isHttp(final String s) {
		if (s == null)
			return false;
		return s.startsWith(HTTP);
	}

	public static boolean isHttps(final String s) {
		if (s == null)
			return false;
		return s.startsWith(HTTPS);
	}

	public static boolean isWap(final Context context) {
		if (context == null)
			return false;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.getExtraInfo() != null) {
			return info.getExtraInfo().endsWith(WAP);
		} else {
			return false;
		}
	}
	
	/**
	 * 根据当前网络状态填充代理
	 * 
	 * @param context
	 * @param httpParams
	 */
	public static void fillProxy(Context context, final HttpParams httpParams) {
		HttpHost proxy = HttpUtils.getProxy(context);
		if(proxy != null){
			httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
	}

	/**
	 * 是否网络可用
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		return NetworkHelpers.isNetworkAvailable(context);
	}

	/**
	 * 获取网络类型，CDMA等
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static String getNetworkType(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = tm.getNetworkType();
		String type = "";
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			type = "1xRTT";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			type = "CDMA";
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			type = "EDGE";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			type = "EVDO 0";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			type = "EVDO A";
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			type = "GPRS";
			break;
		// case TelephonyManager.NETWORK_TYPE_HSDPA:
		// type = "HSDPA";
		// break;
		// case TelephonyManager.NETWORK_TYPE_HSPA:
		// type = "HSPA";
		// break;
		// case TelephonyManager.NETWORK_TYPE_HSUPA:
		// type = "HSUPA";
		// break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			type = "UMTS";
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
		default:
			type = "UNKNOWN";
			break;
		}
		return type;
	}

	/**
	 * Test if wifi is activated
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		return NetworkHelpers.isUsingWifiNetwork(context);
	}

	public static String getProxyUrl(Context context) {
		String proxyUrl = null;
		if (!isWifi(context))
			proxyUrl = android.net.Proxy.getDefaultHost();
		return proxyUrl;
	}

}
