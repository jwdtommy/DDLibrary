package com.dd.framework.services;


public class ApiException extends RuntimeException {
private int resultCode;
    public ApiException(int resultCode) {
    this.resultCode=resultCode;
    }

    private ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @return
     */
    public String getApiExceptionMessage() {
        return resultCode + "";
    }
}

