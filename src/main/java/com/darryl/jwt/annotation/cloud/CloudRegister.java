package com.darryl.jwt.annotation.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @Auther: Darryl
 * @Description: 注册 @Cloud 代理类到Spring IOC中
 * @Date: 2020/07/12
 */
@Configuration
public class CloudRegister implements BeanDefinitionRegistryPostProcessor {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final static String BASE_PACKAGE = "com.darryl.jwt";

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
		Set<BeanDefinitionHolder> beanDefinitionHolders = new CloudScanner(beanDefinitionRegistry).scanPackage(StringUtils.tokenizeToStringArray(BASE_PACKAGE,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		if (CollectionUtils.isEmpty(beanDefinitionHolders)) {
			log.error("no beanDefinition found in {}", BASE_PACKAGE);
		}
		for (BeanDefinitionHolder holder : beanDefinitionHolders) {
			GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
			definition.getPropertyValues().add("interfaceName", definition.getBeanClass());
			definition.setBeanClass(CloudFactoryBean.class);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
		// nothing to do
	}

	public static void main(String[] args) {
		String[] strings = StringUtils.tokenizeToStringArray(BASE_PACKAGE, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		for (String str : strings) {
			System.out.println(str);
		}
	}
}
