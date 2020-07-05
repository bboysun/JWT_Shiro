package com.darryl.jwt.annotation.darryl;

import java.lang.reflect.Method;

/**
 * @Auther: Darryl
 * @Description: test hello annotation
 * @Date: 2020/07/04
 */
public class Test {

	@Hello("darryl")
	public static void main(String[] args) throws NoSuchMethodException {
		Class clazz = Test.class;
		Method method = clazz.getMethod("main", String[].class);
		Hello hello = method.getAnnotation(Hello.class);
		System.out.println(hello.value());
	}

}
