package com.darryl.jwt.annotation.cloud;

import org.springframework.beans.factory.FactoryBean;

/**
 * @Auther: Darryl
 * @Description: cloud factory bean
 * @Date: 2020/07/19
 */
public class CloudFactoryBean<T> implements FactoryBean<T> {
	@Override
	public T getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}
}
