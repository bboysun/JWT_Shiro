package com.darryl.jwt.annotation.cloud;

import com.darryl.jwt.annotation.cloud.annotation.*;
import com.darryl.jwt.annotation.cloud.enums.Protocol;
import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import com.darryl.jwt.annotation.cloud.utils.LazyMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @Auther: Darryl
 * @Description: cloud 代理类
 * @Date: 2020/08/23
 */
@Slf4j
public class CloudProxy implements InvocationHandler {

	// 占位符前缀
	public static final String PLACEHOLDER_PREFIX = "${";
	// 占位符后缀
	public static final String PLACEHOLDER_SUFFIX = "}";
	// 被代理类interface上的注解
	private Map<String, Object> classAnnotations;
	// 缓存调用方法，避免每次通过反射获取
	private LazyMap<Method, CloudMethod> cloudMethodMap = new LazyMap<Method, CloudMethod>() {
		@Override
		protected CloudMethod load(Method key) {
			return buildCloudMethod(key);
		}
	};

	private static final String CLOUD_NAME = Cloud.class.getSimpleName();

	// 构造函数
	public CloudProxy(Object[] classAnnotations) {
		this.classAnnotations = new HashMap<>();
		for (Object classAnnotation : classAnnotations) {
			this.classAnnotations.put(((Annotation) classAnnotation).annotationType().getSimpleName(), classAnnotation);
		}
	}



	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Cloud proxyCloud = (Cloud) checkNotNull(classAnnotations.get(CLOUD_NAME));
		CloudMethod cloudMethod = cloudMethodMap.get(method);
		Protocol cloudProtocol = cloudMethod.getMethodMapping().protocol();
		int retry = 3;
		while (retry > 0) {
			try {
				return CloudHttpClient.execute(cloudProtocol, cloudMethod,
						buildRequestUrl(proxyCloud, cloudMethod.getMethodMapping(),args));
			} catch (Exception e) {
				retry--;
				if (retry == 0) {
					log.error("http 异常： ", e);
					throw new RuntimeException(e);
				}
			}
		}
		throw new RuntimeException("调用轮询3次后异常");
	}

	private String buildRequestUrl(Cloud proxyCloud, CloudMapping methodMapping, Object[] args) {
		String path = parseKey(methodMapping.uri());
		String host = parseKey(proxyCloud.host());
		log.info("request url is {}", host + path);
		return host + path;
	}

	private String parseKey(String key) {
		return key.startsWith(PLACEHOLDER_PREFIX) ? key.substring(key.indexOf(PLACEHOLDER_PREFIX) + 2,
				key.indexOf(PLACEHOLDER_SUFFIX)) : key;
	}

	private CloudMethod buildCloudMethod(Method method) {
		CloudMapping methodMapping = checkNotNull(method.getAnnotation(CloudMapping.class));
		return CloudMethod.builder().methodName(method.getName())
				.methodMapping(methodMapping)
				.parameters(buildParamNames(method))
				.pathParameters(buildPathParamNames(method))
				.postSerializeMethod(buildPostParam(method))
				.requestMethod(methodMapping.requestMethod())
				.responseSerializeMethod(methodMapping.response())
				.returnType(method.getGenericReturnType()).build();
	}

	private Map<String, Integer> buildPathParamNames(Method method) {
		Map<String, Integer> pathParameters = Maps.newHashMap();
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(CloudPathParam.class)) {
					pathParameters.put(((CloudPathParam) annotation).value(),
							((CloudPathParam) annotation).argIndex());
				}
			}
		}
		return pathParameters;
	}

	/**
	 * 构建post参数
	 * @param method
	 * @return
	 */
	private SerializeMethod buildPostParam(Method method) {
		SerializeMethod serialize = null;
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(CloudPostParam.class)) {
					serialize = ((CloudPostParam) annotation).serialize();
				}
			}
		}
		return serialize;
	}

	/**
	 * 构建参数名字
	 * @param method
	 * @return
	 */
	private List<String> buildParamNames(Method method) {
		List<String> paramList = Lists.newArrayList();
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(CloudParam.class)) {
					paramList.add(((CloudParam) annotation).value());
				}
			}
		}
		return paramList;
	}
}
