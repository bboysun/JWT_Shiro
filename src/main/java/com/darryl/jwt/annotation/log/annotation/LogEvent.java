package com.darryl.jwt.annotation.log.annotation;

import com.darryl.jwt.annotation.log.enums.EventType;
import com.darryl.jwt.annotation.log.enums.ModuleType;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: 定义日志的详细内容。如果此注解注解在类上，则这个参数做为类全部方法的默认值。如果注解在方法上，则只对这个方法启作用
 * @Date: 2020/07/05
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogEvent {

	// 日志所属的模块
	ModuleType module() default ModuleType.DEFAULT;
	// 日志事件类型
	EventType event() default EventType.DEFAULT;
	// 描述信息
	String desc() default  "";


}
