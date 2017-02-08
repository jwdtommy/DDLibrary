package com.hyena.framework.config;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.hyena.framework.network.HttpExecutor;
import com.hyena.framework.network.executor.DefaultHttpExecutor;

/**
 * Framework配置
 * @author yangzc
 */
public class FrameworkConfig {

	public static final int APP_SUSUAN = 1;
	public static final int APP_BOX = 2;
	
	private static FrameworkConfig _instance = null;
	private boolean mDebug = false;
	private String mVersionName;
	
	//标识user agent
	private String mUserAgent;
	
	//图片缓存路径
	private File mImageCacheDir;

	private String mGetEncodeKey;
	private String mPostEncodeKey;
	private int mAppType = APP_SUSUAN;

	private FrameworkConfig(){}
	
	public static FrameworkConfig getConfig(){
		if(_instance == null)
			_instance = new FrameworkConfig();
		return _instance;
	}
	
	public static FrameworkConfig init(Context context){
		FrameworkConfig config = FrameworkConfig.getConfig();
		config.initConfig(context);
		return config;
	}
	
	private void initConfig(Context context){

	}
	
	public void setVersionName(String versionName){
		this.mVersionName = versionName;
	}
	
	public String getVersionName(){
		return mVersionName;
	}
	
	/**
	 * 获取user agent
	 */
	public String getUserAgent(){
		return mUserAgent;
	}
	
	/**
	 * 设置user agent
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent){
		this.mUserAgent = userAgent;
	}
	
	/**
	 * 设置应用根目录
	 * @param imageCacheDir
	 */
	public FrameworkConfig setAppRootDir(File imageCacheDir){
		this.mImageCacheDir = imageCacheDir;
		return this;
	}
	
	/**
	 * 设置底层是否是debug状态
	 * @param debug
	 * @return
	 */
	public FrameworkConfig setDebug(boolean debug){
		this.mDebug = debug;
		return this;
	}
	
	/**
	 * 是否是Debug状态
	 * @return
	 */
	public boolean isDebug(){
		return mDebug;
	}
	
	/**
	 * 获得应用根目录
	 * @return
	 */
	public File getAppRootDir(){
		return mImageCacheDir;
	}

	public FrameworkConfig setGetEncodeKey(String key){
		this.mGetEncodeKey = key;
		return this;
	}

	public FrameworkConfig setPostEncodeKey(String key){
		this.mPostEncodeKey = key;
		return this;
	}
	
	public FrameworkConfig setAppType(int appType){
		this.mAppType = appType;
		return this;
	}

	public String getGetEncodeKey(){
		if(TextUtils.isEmpty(mGetEncodeKey)){
			return "";
		}
		return mGetEncodeKey;
	}

	public String getPostEncodeKey(){
		if(TextUtils.isEmpty(mPostEncodeKey)){
			return "";
		}
		return mPostEncodeKey;
	}
	
	public int getAppType(){
		return mAppType;
	}

	private HttpExecutor mHttpExecutor = new DefaultHttpExecutor();
	public HttpExecutor getHttpExecutor() {
		if (mHttpExecutor == null)
			mHttpExecutor = new DefaultHttpExecutor();
		return mHttpExecutor;
	}

	public FrameworkConfig setHttpExecutor(HttpExecutor executor){
		this.mHttpExecutor = executor;
		return this;
	}
}
