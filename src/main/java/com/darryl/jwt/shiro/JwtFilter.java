package com.darryl.jwt.shiro;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: Darryl
 * @Description: jwt filter
 * @Date: 2020/06/21
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	// 检测header里面是否包含Authorization字段，即请求中是否带有token
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String authorization = httpServletRequest.getHeader("Authorization");
		return authorization != null;
	}

	// 执行登入
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String authorization = httpServletRequest.getHeader("Authorization");
		JwtToken token = new JwtToken(authorization);
		// 交给realm进行登入
		getSubject(request, response).login(token);
		// 如果没有出错，就直接返回true
		return true;
	}

	/**
	 * 这里始终返回true，说明都是允许访问
	 * 例如我们提供一个地址 GET /article，登入用户和游客看到的内容是不同的，如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
	 * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入，
	 * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
	 * @param request 请求
	 * @param response 响应
	 * @param mappedValue
	 * @return
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (isLoginAttempt(request, response)) {
			try {
				executeLogin(request, response);
			} catch (Exception e) {
				response401(request, response);
			}
		}
		return true;
	}

	/**
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
		// 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}

	/**
	 * 将非法请求跳转到 /401
	 */
	private void response401(ServletRequest req, ServletResponse resp) {
		try {
			HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
			httpServletResponse.sendRedirect("/401");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
