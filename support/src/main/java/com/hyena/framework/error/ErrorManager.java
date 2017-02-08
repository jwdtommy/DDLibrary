/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.error;

import android.text.TextUtils;

/**
 * 错误管理器
 * @author yangzc on 15/8/20.
 */
public class ErrorManager {

    private static ErrorManager _instance = null;
    private ErrorMap mErrorMap;

    private ErrorManager(){
    }

    public static ErrorManager getErrorManager(){
        if(_instance == null)
            _instance = new ErrorManager();
        return _instance;
    }

    public void registErrorMap(ErrorMap errorMap) {
        this.mErrorMap = errorMap;
    }

    /**
     * 获得错误提示
     * @param errorCode 错误码
     * @param descript 错误描述信息
     * @return
     */
    public String getErrorHint(String errorCode, String descript){
        String hint = mErrorMap.getErrorHint(errorCode, descript);
        if(TextUtils.isEmpty(hint)){
            return "网络连接异常，请稍候再试!";
        }
        return hint;
    }
}
