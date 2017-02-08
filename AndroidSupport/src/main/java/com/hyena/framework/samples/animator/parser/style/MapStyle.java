package com.hyena.framework.samples.animator.parser.style;

import java.util.HashMap;

/**
 * Created by yangzc on 16/4/26.
 */
public class MapStyle {

    private String mId;
    private HashMap<String, String> mStyles;

    public MapStyle(String styleId) {
        this.mId = styleId;
    }

    public String getId() {
        return mId;
    }

    public void setStyle(String key, String value) {
        if (mStyles == null)
            mStyles = new HashMap<String, String>();
        mStyles.put(key, value);
    }

    public String getStyle(String key) {
        if (mStyles == null)
            return "";
        return mStyles.get(key);
    }
}
