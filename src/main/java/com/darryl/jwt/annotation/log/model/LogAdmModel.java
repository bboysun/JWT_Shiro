package com.darryl.jwt.annotation.log.model;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: Darryl
 * @Description: 日志信息
 * @Date: 2020/07/05
 */
@Data
public class LogAdmModel {
	private Long id;
	// 操作用户
	private String userId;
	private String userName;
	// 模块
	private String admModel;
	// 操作
	private String admEvent;
	// 操作内容
	private Date createDate;
	// 操作内容
	private String admOptContent;
	// 备注
	private String desc;
}
