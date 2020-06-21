package com.darryl.jwt.shiro;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Darryl
 * @Description: shiro config class
 * @Date: 2020/06/21
 */
@Configuration
public class ShiroConfig {

	// 构建security manager bean
	@Bean("securityManager")
	public DefaultWebSecurityManager getManager(DarrylRealm darrylRealm) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		// 注入我们自定义的realm
		manager.setRealm(darrylRealm);
		// 关闭shiro自带的session
		DefaultSubjectDAO dao = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		dao.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		manager.setSubjectDAO(dao);
		return manager;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean factory(DefaultWebSecurityManager manager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		// 注入我们自定一的filter
		Map<String, Filter> filterMap = new HashMap<>();
		filterMap.put("jwt", new JwtFilter());
		factoryBean.setFilters(filterMap);
		// 注入security manager
		factoryBean.setSecurityManager(manager);
		// 注入未授权URI
		factoryBean.setUnauthorizedUrl("/401");
		// 自定义路由规则，所有的请求都过我们自定义的filter；/401的请求不过我们自定义的filter
		Map<String, String> filterRuleMap = new HashMap<>();
		filterRuleMap.put("/**", "jwt");
		filterRuleMap.put("/401", "anon");
		factoryBean.setFilterChainDefinitionMap(filterRuleMap);
		return factoryBean;
	}

	/**
	 * 下面的代码是添加注解支持
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		// 强制使用cglib，防止重复代理和可能引起代理出错的问题
		// https://zhuanlan.zhihu.com/p/29161098
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		return defaultAdvisorAutoProxyCreator;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}
}
