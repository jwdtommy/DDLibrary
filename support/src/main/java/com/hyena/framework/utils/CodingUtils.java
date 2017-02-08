/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.utils;

/**
 * 编译环境获取相关参数通用类
 * @author yangzc
 *
 */
public class CodingUtils {

	/**
	 * 获得工程目录
	 * @return
	 */
	public static String getProjectPath(){
		return System.getProperty("user.dir");
	}
	
}
