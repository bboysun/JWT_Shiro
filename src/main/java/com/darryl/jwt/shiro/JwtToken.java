package com.darryl.jwt.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Auther: Darryl
 * @Description: 因为我们的token中包含了用户信息，该类基本可以认为是用户名和密码的载体，简单只需要一个token成员变量即可
 *               用于realm登录使用
 * @Date: 2020/06/21
 */
public class JwtToken implements AuthenticationToken {

	private String token;

	public JwtToken(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}
}
