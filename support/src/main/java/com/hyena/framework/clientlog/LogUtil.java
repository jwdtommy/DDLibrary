package com.hyena.framework.clientlog;

/**
 * 日志控制
 * @author yangzc
 * 
 */
public class LogUtil {
	/**
	 * 默认的文库日志Tag标签
	 */
	public final static String DEFAULT_TAG = "KnowBox";

	private static Logger mLogger = Logger.getLogger("KnowBox.log");
	
	public static final String KEYWORD_lOG_2_FILE = "knowbox_1";
	public static final String KEYWORD_lOGCAT_2_SDCARD = "knowbox_2";
	
	/**
	 * 设置是否开启日志
	 * @param debugable
	 */
	public static void setDebug(boolean debugable){
		if(debugable)
			mLogger.traceOn();
		else
			mLogger.traceOff();
	}
	
	/**
	 * 是否调试模式
	 * @return
	 */
	public static boolean isDebug(){
		return mLogger.isTracing();
	}
	
	/**
	 * 设置日志级别
	 * @param level
	 */
	public static void setLevel(int level){
		mLogger.setLevel(level);;
	}
	
	/**
	 * 打印debug级别的log
	 * 
	 * @param tag tag标签
	 * @param msg 内容
	 */
	public static void d(String tag, String msg) {
		if (mLogger != null && tag != null) {
			mLogger.d(tag, msg);
		}
	}

	/**
	 * 打印debug级别的log
	 *
	 * @param tag tag标签
	 * @param msg 内容
	 */
	public static void d(Object tag, String msg) {
		if (mLogger != null && tag != null) {
			mLogger.d(tag.getClass().getSimpleName(), msg);
		}
	}

	/**
	 * 打印warning级别的log
	 * @param tag tag标签
	 * @param msg 内容
	 */
	public static void w(String tag, String msg) {
		if (mLogger != null && tag != null) {
			mLogger.w(tag, msg);
		}
	}

	/**
	 * 打印error级别的log
	 * @param tag tag标签
	 */
	public static void e(String tag, Throwable tr) {
		if (mLogger != null && tag != null) {
			mLogger.e(tag, tr);
		}
	}
	
	/**
	 * 打印error级别的log
	 * @param tag tag标签
	 */
	public static void e(String tag, String msg){
		if (mLogger != null && tag != null) {
			mLogger.e(tag, msg);
		}
	}
	
	/**
	 * 打印error级别的log
	 * @param tag tag标签
	 */
	public static void e(String tag, String msg, Throwable tr){
		if (mLogger != null && tag != null) {
			mLogger.e(tag, msg, tr);
		}
	}

	/**
	 * 打印info级别的log
	 * 
	 * @param tag
	 *            tag标签
	 * @param str
	 *            内容
	 */
	public static void i(String tag, String str) {
		if (mLogger != null && tag != null) {
			mLogger.i(tag, str);
		}
	}
	
	/**
	 * 打印verbose级别的log
	 * @param tag tag标签
	 * @param str 内容
	 */
	public static void v(String tag, String str) {
		if (mLogger != null && tag != null) {
			mLogger.v(tag, str);
		}
	}
	
	//********************************************************************************//

	/**
	 * 打印info级别的log
	 * @param str 内容
	 */
	public static void i(String str) {
		i(DEFAULT_TAG, str);
	}

	/**
	 * 打印verbose级别的log
	 * @param str 内容
	 */
	public static void v(String str) {
		v(DEFAULT_TAG, str);
	}
	
	/**
	 * 打印warning级别的log
	 * @param str 内容
	 */
	public static void w(String str) {
		w(DEFAULT_TAG, str);
	}
	
	/**
	 * 打印debug级别的log
	 * @param str 内容
	 */
	public static void d(String str) {
		d(DEFAULT_TAG, str);
	}
	
	/**
	 * 打印Error级别log
	 */
	public static void e(String str){
		e(DEFAULT_TAG, str);
	}

	public static void e(Object o, String s) {
		if (mLogger != null && o != null) {
			mLogger.e(o.getClass().getSimpleName(), s);
		}
	}
}
