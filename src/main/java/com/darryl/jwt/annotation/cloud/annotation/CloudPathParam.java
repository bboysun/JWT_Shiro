package com.darryl.jwt.annotation.cloud.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2021/03/28
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudPathParam {
	String value() default "";
	int argIndex() default 0;
}
