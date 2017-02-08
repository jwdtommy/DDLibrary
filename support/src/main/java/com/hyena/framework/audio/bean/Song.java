/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio.bean;

import java.io.File;
import java.io.Serializable;

import com.hyena.framework.audio.MusicDir;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.security.MD5Util;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 歌曲信息
 *
 * @author yangzc
 */
public class Song implements Serializable {

    // 歌曲远程URL
    private String mUrl;
    private String mLocalPath;
    private boolean mIsOnline;

    public Song(boolean isOnline, String url, String localPath) {
        this.mUrl = url;
        this.mLocalPath = localPath;
        this.mIsOnline = isOnline;
    }

    /**
     * 获得本地路径
     *
     * @return
     */
    public File getLocalFile() {
        if(TextUtils.isEmpty(mLocalPath)) {
            return new File(MusicDir.getMusicDir(), MD5Util.encode(mUrl)
                + ".mp3");
        }
        return new File(mLocalPath);
    }

    public String getUrl() {
        return mUrl;
    }

    /**
     * 是否是在线歌曲
     *
     * @return
     */
    public boolean isOnline() {
        return mIsOnline;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            if (mUrl != null)
                return mUrl.equals(((Song) o).mUrl);
        }
        return super.equals(o);
    }
}
