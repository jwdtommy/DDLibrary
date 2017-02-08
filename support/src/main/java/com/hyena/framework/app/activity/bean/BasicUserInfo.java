/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.hyena.framework.app.activity.bean;

import java.io.Serializable;

/**
 * 用户基本信息
 * @author yangzc
 *
 */
public class BasicUserInfo implements Serializable {

	/** 序列号 */
	private static final long serialVersionUID = -5352521149556129447L;

	//用户ID
	public String userId;
	//用户姓名
	public String userName;
	//用户唯一标识
	public String token;
	
	public BasicUserInfo(){}
	
	public BasicUserInfo(String userId, String userName, String token){
		this.userId = userId;
		this.userName = userName;
		this.token = token;
	}
}
