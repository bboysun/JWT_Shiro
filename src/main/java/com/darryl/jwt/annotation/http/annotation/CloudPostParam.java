package com.darryl.jwt.annotation.http.annotation;

import com.darryl.jwt.annotation.http.enums.SerializeMethod;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: request parameter when request type is post
 * @Date: 2020/07/12
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudPostParam {

	// serialized method when request type is post
	SerializeMethod serialize() default SerializeMethod.JSON;
}
