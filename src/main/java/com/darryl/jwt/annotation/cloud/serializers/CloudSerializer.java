package com.darryl.jwt.annotation.cloud.serializers;

import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;

import java.lang.reflect.Type;

/**
 * @Auther: Darryl
 * @Description: 序列化方式
 * @Date: 2020/09/05
 */
public interface CloudSerializer {

	String serialize(Object source);

	Object desrialize(Type type, String source);

	SerializeMethod method();

	String contentType();
}
