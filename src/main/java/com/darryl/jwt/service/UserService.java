package com.darryl.jwt.service;

import com.darryl.jwt.annotation.log.annotation.LogEnable;
import com.darryl.jwt.annotation.log.annotation.LogEvent;
import com.darryl.jwt.annotation.log.enums.ModuleType;
import com.darryl.jwt.model.UserBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Auther: Darryl
 * @Description: user service
 * @Date: 2020/06/21
 */
@Component
public class UserService {

	public UserBean getUser(String username) {
		// 没有此用户直接返回null
		if (! DataSource.getData().containsKey(username))
			return null;

		UserBean user = new UserBean();
		Map<String, String> detail = DataSource.getData().get(username);

		user.setUsername(username);
		user.setPassword(detail.get("password"));
		user.setRole(detail.get("role"));
		user.setPermission(detail.get("permission"));
		return user;
	}
}
