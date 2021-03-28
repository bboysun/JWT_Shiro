package com.darryl.jwt.annotation.cloud;

import com.darryl.jwt.annotation.cloud.annotation.Cloud;
import com.darryl.jwt.annotation.cloud.annotation.CloudMapping;
import com.darryl.jwt.annotation.cloud.enums.Protocol;
import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import com.darryl.jwt.annotation.cloud.utils.LazyMap;

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
		return null;
	}

	private CloudMethod buildCloudMethod(Method method) {
		CloudMapping methodMapping = checkNotNull(method.getAnnotation(CloudMapping.class));
		return CloudMethod.builder().methodName(method.getName())
				.methodMapping(methodMapping)
				.parameters(buildParamNames(method))
				.postSerializeMethod(buildPostParam(method))
				.requestMethod(methodMapping.requestMethod())
				.responseSerializeMethod(methodMapping.response())
				.returnType(method.getGenericReturnType()).build();
	}

	/**
	 * 构建post参数
	 * @param method
	 * @return
	 */
	private SerializeMethod buildPostParam(Method method) {
		return null;
	}

	/**
	 * 构建参数名字
	 * @param method
	 * @return
	 */
	private List<String> buildParamNames(Method method) {
		return null;
	}
}
