package com.darryl.jwt.annotation.log.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: 定义日志开关
 * @Date: 2020/07/05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogEnable {

	// 是否启用log，默认是启动
	boolean logEnable() default true;
}
