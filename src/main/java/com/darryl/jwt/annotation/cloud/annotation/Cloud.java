package com.darryl.jwt.annotation.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: cloud annotation from spring cloud，思想来自于Spring cloud，通过注解封装好http请求，
 * 用于获取请求服务的host地址
 * @Date: 2020/07/12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cloud {

	// request host
	String host() default "";

}
