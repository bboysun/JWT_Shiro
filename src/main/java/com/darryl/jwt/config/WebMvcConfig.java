package com.darryl.jwt.config;

import com.darryl.jwt.license.LicenseCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc config
 *
 * @Author: darrylsun
 * @Description: web mvc config
 * @Date: 2023/8/30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LicenseCheckInterceptor licenseCheckInterceptor;
    private String[] licenseIncludePathPatterns = {"/**"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 运行时校验 license
        registry.addInterceptor(licenseCheckInterceptor).addPathPatterns(licenseIncludePathPatterns);
    }
}
