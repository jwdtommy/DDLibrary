package com.dd.news.entry.base;

import com.dd.framework.base.BaseResult;

/**
 * Created by adong on 16/8/22.
 */
public class ShowApiResult<T> extends BaseResult {
	/**
	 * 易源返回标志,0为成功，其他为失败。
	 * 0成功
	 * -1，系统调用错误
	 * -2，可调用次数或金额为0
	 * -3，读取超时
	 * -4，服务端返回数据解析错误
	 * -5，后端服务器DNS解析错误
	 * -6，服务不存在或未上线
	 * -1000，系统维护
	 * -1002，showapi_appid字段必传
	 * -1003，showapi_sign字段必传
	 * -1004，签名sign验证有误
	 * -1005，showapi_timestamp无效
	 * -1006，app无权限调用接口
	 * -1007，没有订购套餐
	 * -1008，服务商关闭对您的调用权限
	 * -1010，找不到您的应用
	 * -1011，子授权app_child_id无效
	 * -1012，子授权已过期或失效
	 * -1013，子授权ip受限
	 **/
	private int showapi_res_code = -1;
	private String showapi_res_error;
	private T showapi_res_body;

	@Override
	public void transfrom() {
		if (showapi_res_code == 0) {
			error_code = showapi_res_code;
		} else {//简单做了个对应关系的处理，实际上可以将异常对应的更细
			error_code = BaseResult.NO_DATA;
		}
		data=showapi_res_body;
	}

	public int getShowapi_res_code() {
		return showapi_res_code;
	}

	public void setShowapi_res_code(int showapi_res_code) {
		this.showapi_res_code = showapi_res_code;
	}

	public String getShowapi_res_error() {
		return showapi_res_error;
	}

	public void setShowapi_res_error(String showapi_res_error) {
		this.showapi_res_error = showapi_res_error;
	}

	public T getShowapi_res_body() {
		return showapi_res_body;
	}

	public void setShowapi_res_body(T showapi_res_body) {
		this.showapi_res_body = showapi_res_body;
	}

}
