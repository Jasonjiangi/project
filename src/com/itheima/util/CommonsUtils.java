package com.itheima.util;

import java.util.UUID;

/**
 * 获得uuid类
* @author YEGC
* @version 1.0
* @data 2019年4月26日 下午9:56:33
* @remark Be Yourself
*/
public class CommonsUtils {
	
	// 生成uuid方法
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
}
