package com.darryl.jwt.annotation.log.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: 如果注解在方法上，则整个方法的参数以json的格式保存到日志中。如果此注解同时注解在方法和类上，则方法上的注解会覆盖类上的值
 * @Date: 2020/07/05
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogKey {
	// key的名称
	String keyName() default "";
	// 此字段是否是本次操作的userId
	boolean isUserId() default false;
	// 是否加入到日志中
	boolean isLog() default true;
}
