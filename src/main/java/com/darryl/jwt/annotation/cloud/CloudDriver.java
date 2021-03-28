package com.darryl.jwt.annotation.cloud;

import com.darryl.jwt.annotation.cloud.enums.SerializeMethod;
import com.darryl.jwt.annotation.cloud.serializers.CloudSerializer;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2021/03/28
 */
@Component
public class CloudDriver implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {
	private final Map<SerializeMethod, CloudSerializer> serializerMap = Maps.newHashMap();

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		CloudHttpClient.initSerializers(serializerMap);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (!(bean instanceof CloudSerializer)) {
			return bean;
		}
		CloudSerializer serializer = (CloudSerializer) bean;
		SerializeMethod method = serializer.method();
		if (serializerMap.get(method) != null) {
			throw new RuntimeException("Duplicate cloud method is " + method.name());
		}
		serializerMap.put(method, serializer);
		return bean;
	}
}
