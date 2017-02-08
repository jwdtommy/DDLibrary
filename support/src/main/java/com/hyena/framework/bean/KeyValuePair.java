package com.hyena.framework.bean;

import android.text.TextUtils;

/**
 * 键值对
 * Created by yangzc on 16/7/12.
 */
public class KeyValuePair {

    private String mKey;
    private String mValue;

    public KeyValuePair() {
    }

    public KeyValuePair(String key, String value) {
        this.mKey = key;
        this.mValue = value;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    public String getKey() {
        if (TextUtils.isEmpty(mKey))
            mKey = "";
        return mKey;
    }

    public String getValue() {
        if (TextUtils.isEmpty(mValue))
            mValue = "";
        return mValue;
    }
}
