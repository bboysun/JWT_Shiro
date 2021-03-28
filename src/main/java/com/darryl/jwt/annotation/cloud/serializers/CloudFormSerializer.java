package com.darryl.jwt.annotation.cloud.serializers;

import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import com.darryl.jwt.annotation.cloud.utils.MapParamGeneUtil;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2021/03/28
 */
@Component
@Slf4j
public class CloudFormSerializer implements CloudSerializer {
	private static final Joiner.MapJoiner PARAM_JOINER = Joiner.on("&").withKeyValueSeparator("=");

	@Override
	public String serialize(Object source) {
		try {
			Map<String, String> argsMaps = MapParamGeneUtil.generateMapParam(source);
			return PARAM_JOINER.join(argsMaps);
		} catch (IllegalAccessException e) {
			log.error("form serialize error: ", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object desrialize(Type type, String source) {
		throw new UnsupportedOperationException("Form deserialize unsupported");
	}

	@Override
	public SerializeMethod method() {
		return SerializeMethod.FORM;
	}

	@Override
	public String contentType() {
		return "application/x-www-form-urlencoded";
	}
}
