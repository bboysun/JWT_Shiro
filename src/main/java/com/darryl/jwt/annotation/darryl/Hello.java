package com.darryl.jwt.annotation.darryl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: Darryl
 * @Description: 自定义注解 hello
 * @Date: 2020/07/04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hello {
	String value();
}
