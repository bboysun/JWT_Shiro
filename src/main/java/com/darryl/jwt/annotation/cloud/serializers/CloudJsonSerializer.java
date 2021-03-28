package com.darryl.jwt.annotation.cloud.serializers;

import com.alibaba.fastjson.JSON;
import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2021/03/28
 */
@Component
public class CloudJsonSerializer implements CloudSerializer {
	@Override
	public String serialize(Object source) {
		return JSON.toJSONString(source);
	}

	@Override
	public Object desrialize(Type type, String source) {
		if (type.equals(Void.TYPE)) {
			return null;
		}
		return JSON.parseObject(source, type);
	}

	@Override
	public SerializeMethod method() {
		return SerializeMethod.JSON;
	}

	@Override
	public String contentType() {
		return "application/json";
	}
}
