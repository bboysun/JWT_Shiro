package com.darryl.jwt.annotation.cloud;

import com.darryl.jwt.annotation.cloud.annotation.Cloud;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * @Auther: Darryl
 * @Description: 首先在spring boot 启动时，扫描 @Cloud 注解标注的类
 * @Date: 2020/07/12
 */
public class CloudScanner extends ClassPathBeanDefinitionScanner {

	private List<TypeFilter> filters = Lists.newArrayList(new AnnotationTypeFilter(Cloud.class));

	@PostConstruct
	public void init() {
		if (!CollectionUtils.isEmpty(filters)) {
			for (TypeFilter filter : filters) {
				addIncludeFilter(filter);
			}
		}
	}

	public CloudScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	public Set<BeanDefinitionHolder> scanPackage(String... basePackage) {
		return super.doScan(basePackage);
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
	}

}
