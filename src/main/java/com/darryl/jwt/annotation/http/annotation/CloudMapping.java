package com.darryl.jwt.annotation.http.annotation;

import com.darryl.jwt.annotation.http.enums.Protocol;
import com.darryl.jwt.annotation.http.enums.SerializeMethod;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * @Auther: Darryl
 * @Description: 用于指定具体发送请求类型，请求的URI等信息
 * @Date: 2020/07/12
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudMapping {

	// 请求uri
	String uri() default "";

	// request method default is post
	RequestMethod requestMethod() default RequestMethod.POST;

	// request protocol
	Protocol protocol() default Protocol.HTTP;

	// serialize method
	SerializeMethod serializeMethod() default SerializeMethod.JSON;
}
