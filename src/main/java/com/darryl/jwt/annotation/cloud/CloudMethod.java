package com.darryl.jwt.annotation.cloud;

import com.darryl.jwt.annotation.cloud.annotation.CloudMapping;
import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @Auther: Darryl
 * @Description: cloud method define class
 * @Date: 2020/08/23
 */
@Getter
@Builder
public class CloudMethod {

	private final String methodName;
	private final List<String> parameters;
	private final SerializeMethod postSerializeMethod;
	private final CloudMapping methodMapping;
	private final RequestMethod requestMethod;
	private final SerializeMethod responseSerializeMethod;
	private final Type returnType;

	public CloudMethod(String methodName, List<String> parameters, SerializeMethod postSerializeMethod, CloudMapping methodMapping
			, RequestMethod requestMethod, SerializeMethod responseSerializeMethod, Type returnType) {
		this.methodName = methodName;
		this.parameters = parameters;
		this.postSerializeMethod = postSerializeMethod;
		this.methodMapping = methodMapping;
		this.requestMethod = requestMethod;
		this.responseSerializeMethod = responseSerializeMethod;
		this.returnType = returnType;
	}
}
