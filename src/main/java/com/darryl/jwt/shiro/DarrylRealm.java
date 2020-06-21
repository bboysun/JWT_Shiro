package com.darryl.jwt.shiro;

import com.darryl.jwt.model.UserBean;
import com.darryl.jwt.service.UserService;
import com.darryl.jwt.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: Darryl
 * @Description: realm 的用于处理用户是否合法的这一块，需要我们自己实现。
 * @Date: 2020/06/21
 */
@Service
public class DarrylRealm extends AuthorizingRealm {

	private UserService userService;

	// set 注入方法
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	// 必须重写此方法，要将我们自定义的token满足支持
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}

	// 只有当需要检测用户权限的时候才会调用此方法，例如校验角色，校验权限等
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String username = JwtUtil.getUserName(principalCollection.toString());
		UserBean user = userService.getUser(username);
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addRole(user.getRole());
		Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
		simpleAuthorizationInfo.addStringPermissions(permission);
		return simpleAuthorizationInfo;
	}

	// 使用此方法进行用户名正确与否验证，错误抛出异常即可。
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		String token = (String) authenticationToken.getCredentials();
		String userName = JwtUtil.getUserName(token);
		// token非法
		if (StringUtils.isEmpty(userName)) {
			throw new AuthenticationException("token invalid");
		}
		UserBean user = userService.getUser(userName);
		// 没有该用户
		if (user == null) {
			throw new AuthenticationException("User didn't existed!");
		}
		// 用户名和密码校验不通过
		if (!JwtUtil.verify(token, userName, user.getPassword())) {
			throw new AuthenticationException("Username or password error");
		}
		return new SimpleAuthenticationInfo(token, token, "darryl_realm");
	}
}
