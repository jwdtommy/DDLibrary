package com.hyena.framework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;

/**
 * 应用框架层Preference
 * @author yangzc
 */
public class AppPreferences {
	private static String PREFERENCE_NAME = "app_base_pref";

	private static String mPrefPrefix = "";

	private static AppPreferences mPreferences;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;

	private AppPreferences(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}

	public static synchronized AppPreferences getPreferences() {
		if (mPreferences == null) {
			mPreferences = new AppPreferences(BaseApp.getAppContext());
		}
		return mPreferences;
	}

	public static AppPreferences getInstance() {
		return getPreferences();
	}

	public SharedPreferences getSharedPreference() {
		return mSharedPreferences;
	}

	public static void setPrefPerfix(String prefix) {
		mPrefPrefix = prefix;
	}

	public void registerSharePreferencesListener(
			OnSharedPreferenceChangeListener listener) {
		mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unRegisterPreferencesListener(OnSharedPreferenceChangeListener listener) {
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	//============================================================

	private String getKey(String key) {
		return mPrefPrefix + "_" + key;
	}

	private String getString(String key) {
		return mSharedPreferences.getString(getKey(key), "");
	}

	private void setString(String key, String value) {
		mEditor.putString(getKey(key), value);
		mEditor.commit();
	}

	private int getIntValue(String key, int defaultValue) {
		return mSharedPreferences.getInt(getKey(key), defaultValue);
	}

	private void setIntValue(String key, int value) {
		mEditor.putInt(getKey(key), value);
		mEditor.commit();
	}

	private long getLong(String key) {
		return mSharedPreferences.getLong(getKey(key), -1);
	}

	private void setLong(String key, long value) {
		mEditor.putLong(getKey(key), value);
		mEditor.commit();
	}

	private boolean getBooleanValue(String key, boolean value) {
		return mSharedPreferences.getBoolean(getKey(key), value);
	}

	private void setBooleanValue(String key, boolean value) {
		mEditor.putBoolean(getKey(key), value);
		mEditor.commit();
	}

	private List<String> getStringListValue(String key) {
		int size = mSharedPreferences.getInt(getKey(key + "_size"), 0);
		if (size == 0)
			return null;
		List<String> lists = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			String string = mSharedPreferences.getString(getKey(key + "_" + i), null);
			lists.add(string);
		}
		return lists;
	}

	private void setStringListValue(String key, List<String> list) {
		if (list == null || list.isEmpty()) {
			mEditor.remove(getKey(key + "_size"));
			mEditor.commit();
			return;
		}
		mEditor.putInt(getKey(key + "_size"), list.size()); /* sKey is an array */
		for (int i = 0; i < list.size(); i++) {
			mEditor.remove(getKey(key + "_" + i));
			mEditor.putString(getKey(key + "_" + i), list.get(i));
		}
		mEditor.commit();
	}

	private void setIntListValue(String key, List<Integer> list) {
		if (list == null || list.isEmpty()) {
			mEditor.remove(getKey(key + "_size"));
			mEditor.commit();
			return;
		}
		mEditor.putInt(getKey(key + "_size"), list.size()); /* sKey is an array */
		for (int i = 0; i < list.size(); i++) {
			mEditor.remove(getKey(key + "_" + i));
			mEditor.putInt(getKey(key + "_" + i), list.get(i));
		}
		mEditor.commit();
	}

	private List<Integer> getIntListValue(String key) {
		int size = mSharedPreferences.getInt(getKey(key + "_size"), 0);
		if (size == 0)
			return null;
		List<Integer> lists = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			int string = mSharedPreferences.getInt(getKey(key + "_" + i), 0);
			lists.add(string);
		}
		return lists;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private Set<String> getStringSetValue(String key) {
		return mSharedPreferences.getStringSet(getKey(key), null);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setStringSetValue(String key, Set<String> set) {
		mEditor.putStringSet(getKey(key), set);
		mEditor.commit();
	}

	//============================================================

	public static void setInt(String key, int value) {
		getInstance().setIntValue(key, value);
	}

	public static int getInt(String key) {
		return getInstance().getIntValue(key, -1);
	}

	public static int getInt(String key, int defaultValue) {
		return getInstance().getIntValue(key, defaultValue);
	}

	public static void setStringValue(String key, String value) {
		getInstance().setString(key, value);
	}

	public static String getStringValue(String key) {
		return getInstance().getString(key);
	}

	public static void setBoolean(String key, boolean value) {
		getInstance().setBooleanValue(key, value);
	}

	public static boolean getBoolean(String key, boolean value) {
		return getInstance().getBooleanValue(key, value);
	}

	public static void setLongValue(String key, Long value) {
		getInstance().setLong(key, value);
	}

	public static Long getLongValue(String key) {
		return getInstance().getLong(key);
	}

	public static void setStringSet(String key, Set<String> set) {
		getInstance().setStringSetValue(key, set);
	}

	public static Set<String> getStringSet(String key) {
		return getInstance().getStringSetValue(key);
	}

	public static void setStringList(String key, List<String> list) {
		getInstance().setStringListValue(key, list);
	}

	public static List<String> getStringList(String key) {
		return getInstance().getStringListValue(key);
	}

	public static void setIntList(String key, List<Integer> list) {
		getInstance().setIntListValue(key, list);
	}

	public static List<Integer> getIntList(String key) {
		return getInstance().getIntListValue(key);
	}

}
