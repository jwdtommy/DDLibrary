package com.hyena.framework.datacache;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.cache.Cacheable;
import com.hyena.framework.debug.DebugUtils;

/**
 * 所以数据模型的基类
 */
public class BaseObject implements Cloneable, Cacheable {

	public final static int NO_DATA = 0;/** 无数据 */
	public final static int OK = 1;/** 数据返回正常 */
	public final static int ERROR_NETWORK_UNAVAILABLE = 2;
	public final static int ERROR_INVALID_PARAMETER = 3;
	public final static int ERROR_AUTHORIZATION_FAIL = 4;
	public final static int ERROR_NETWORK_TIMEOUT = 5;
	public final static int ERROR_HTTP_REQUEST_ERROR = 6;

	protected int mErrorCode = NO_DATA;
	private String mRawResultCode;
	private int mStatusCode;

	protected String mErrorDescription;
	/** JSON字符串的弱引用，用来暂存JSON以供缓存 */
	transient private WeakReference<String> mJsonReference;

	/**
	 * 获取错误码<br>
	 * 
	 * 注意：使用错误码判断请求成功与否时，若某数据对象存在子元素，只能判断最外层对象的错误码判断，子元素对象获取到的ErrorCode
	 * 不能标识本次请求的成功与否
	 * 
	 * @return 错误码
	 */
	public int getErrorCode() {
		return mErrorCode;
	}
	
	public String getRawResult(){
		return mRawResultCode;
	}
	
	public void setStatusCode(int statusCode){
		this.mStatusCode = statusCode;
	}
	
	public int getStatusCode(){
		return mStatusCode;
	}

	/**
	 * 更新错误码
	 * 
	 * @param error
	 *            错误码
	 */
	public void setErrorCode(int error) {
		mErrorCode = error;
	}

	/**
	 * 重置数据状态
	 */
	public void resetState() {
		mErrorCode = NO_DATA;
	}

	/**
	 * 数据是否可用
	 *
	 * @return 数据是否可用
	 */
	public boolean isAvailable() {
		return mErrorCode == OK;
	}

	/**
	 * 获取错误描述<br/>
	 * 
	 * 该错误描述字符串有可能为空
	 * 
	 * @return 错误描述
	 */
	public String getErrorDescription() {
		return mErrorDescription;
	}

	public void setErrorDescription(String description) {
		mErrorDescription = description;
	}

	public WeakReference<String> getmJsonReference() {
		return mJsonReference;
	}

	public void setmJsonReference(WeakReference<String> mJsonReference) {
		this.mJsonReference = mJsonReference;
	}

	/**
	 * 获取当前对象对应的json字符串
	 * 
	 * @return JSON
	 */
	public String getJSON() {
		String json = null;
		if (mJsonReference != null)
			json = mJsonReference.get();
		return json;
	}

	final public void parse(String json){
		parse(json, true);
	}
	
	/**
	 * 解析json数据
	 * 
	 * @param json
	 */
	final public void parse(String json, boolean fromCache) {
		LogUtil.v(fromCache? "BaseObjectCache" : "BaseObject", "parse: " + json);

		DebugUtils.debugTxt("Response: " + json);
		if (TextUtils.isEmpty(json)) {
			setErrorCode(BaseObject.NO_DATA);
			return;
		}

		JSONObject obj = null;
		try {
			obj = new JSONObject(json);
			mJsonReference = new WeakReference<String>(json);
			mRawResultCode = obj.optString("code");
			if(isDataValid(obj)){
				setErrorCode(OK);
				parse(obj);
			} else {
				setErrorCode(NO_DATA);
				parseErrorMsg(obj);
			}
		} catch (Exception e) {
			setErrorCode(BaseObject.NO_DATA);
		}
	}

	public void parse(JSONObject json) {
	}

	/**
	 * 判断返回数据是否合法
	 * @param json
	 * @return
	 */
	protected boolean isDataValid(JSONObject json){
		String resultCode = json.optString("code");
		return "99999".equals(resultCode) || "success".equals(resultCode);
	}
	
	/**
	 * 解析错误信息
	 * @param obj
	 */
	protected void parseErrorMsg(JSONObject obj){
		if(obj.has("data")){
			JSONObject data = obj.optJSONObject("data");
			if(data != null){
				this.mErrorDescription = data.optString("msg");
			}
			if (TextUtils.isEmpty(mErrorDescription)) {
				this.mErrorDescription = obj.optString("msg");
			}
		} else {
			this.mErrorDescription = obj.optString("msg");
		}
	}

	@Override
	public BaseObject clone() {
		BaseObject obj = null;
		try {
			obj = (BaseObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public long calculateMemSize() {
		return 0;
	}

	/**
	 * 判断数据模型是否可用
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isAvailable(BaseObject obj) {
		return obj != null && obj.isAvailable();
	}

	@Override
	public String buildCacheData() {
		return getJSON();
	}

	@Override
	public BaseObject parseCacheData(String data) {
		parse(data, true);
		return this;
	}

	@Override
	public boolean isCacheable() {
		/* 数据正常，且存在可缓存的JSON字符串 */
		return isAvailable() && !TextUtils.isEmpty(getJSON());
	}

}