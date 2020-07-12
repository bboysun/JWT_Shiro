package com.darryl.jwt.annotation.http.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: 请求参数，一般为get请求类型时，标注参数名
 * @Date: 2020/07/12
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudParam {

	// parameter value
	String value() default "";
}
