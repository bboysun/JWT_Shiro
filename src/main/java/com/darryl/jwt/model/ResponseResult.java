package com.darryl.jwt.model;

import lombok.Data;

/**
 * @Auther: Darryl
 * @Description: 响应结果体
 * @Date: 2020/06/21
 */
@Data
public class ResponseResult {
	// 响应消息简述
	private String msg;
	// 响应数据结构体
	private Object data;
}
