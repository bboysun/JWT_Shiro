package com.darryl.jwt.annotation.log.enums;

/**
 * @Auther: Darryl
 * @Description: 日志操作类型，如登陆、添加、删除、更新、删除等
 * @Date: 2020/07/05
 */
public enum EventType {

	DEFAULT("1", "default"),
	ADD("2", "add"),
	UPDATE("3", "update"),
	DELETE_SINGLE("4", "delete-single"),
	LOGIN("10", "login"),
	LOGIN_OUT("11", "login_out");

	EventType(String index, String name) {
		this.name = name;
		this.event = index;
	}

	private String event;
	private String name;

	public String getEvent() {
		return this.event;
	}

	public String getName() {
		return name;
	}

}
