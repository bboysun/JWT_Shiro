package com.darryl.jwt.annotation.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @Auther: Darryl
 * @Description: cloud factory bean
 * @Date: 2020/07/19
 */
public class CloudFactoryBean<T> implements FactoryBean<T> {

	private static final Logger log = LoggerFactory.getLogger(CloudFactoryBean.class);

	private String interfaceName;

	@Override
	public T getObject() throws Exception {
		Class clazz = Class.forName(interfaceName);
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new CloudProxy(clazz.getAnnotations()));
	}

	@Override
	public Class<?> getObjectType() {
		if (interfaceName == null) {
			return null;
		}
		try {
			return (Class<T>) Class.forName(interfaceName);
		} catch (ClassNotFoundException e) {
			log.error("class not found:{}", interfaceName, e);
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
