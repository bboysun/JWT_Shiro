package com.darryl.jwt.model;

import lombok.Data;

/**
 * @Auther: Darryl
 * @Description: user model bean
 * @Date: 2020/06/21
 */
@Data
public class UserBean {
	private String username;

	private String password;

	private String role;

	private String permission;
}
