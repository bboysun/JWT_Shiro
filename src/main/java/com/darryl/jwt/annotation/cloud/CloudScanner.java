package com.darryl.jwt.annotation.cloud;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @Auther: Darryl
 * @Description: 首先在spring boot 启动时，扫描 @Cloud 注解标注的类
 * @Date: 2020/07/12
 */
public class CloudScanner extends ClassPathBeanDefinitionScanner {

	private Class<? extends Annotation> cloudAnnotation;

	public CloudScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> cloudAnnotation) {
		super(registry);
		this.cloudAnnotation = cloudAnnotation;
		super.addIncludeFilter(new AnnotationTypeFilter(cloudAnnotation));
	}

	public Set<BeanDefinitionHolder> scanPackage(String... basePackage) {
		return super.doScan(basePackage);
	}

	// 过滤出我们想要的bean组件
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
	}

}
