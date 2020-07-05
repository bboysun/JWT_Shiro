package com.darryl.jwt.annotation.log.enums;

/**
 * @Auther: Darryl
 * @Description: 日志的功能模块类型，比如我们的例子是学生模块，用户模块等
 * @Date: 2020/07/05
 */
public enum ModuleType {

	DEFAULT("1"), // 默认值
	STUDENT("2"), // 学生模块
	USER("3"); // 用户模块

	ModuleType(String index){
		this.module = index;
	}
	private String module;
	public String getModule(){
		return this.module;
	}

}
