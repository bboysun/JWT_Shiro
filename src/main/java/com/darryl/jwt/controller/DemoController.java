package com.darryl.jwt.controller;

import com.darryl.jwt.model.ResponseResult;
import com.darryl.jwt.model.UserBean;
import com.darryl.jwt.service.UserService;
import com.darryl.jwt.utils.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: Darryl
 * @Description: demo controller for test shiro
 * @Date: 2020/06/21
 */
@RestController
public class DemoController {

	@Autowired
	private UserService userService;

	/**
	 * 登录入口
	 * @param username 用户名
	 * @param password 密码
	 * @return token值给前端
	 */
	@PostMapping(value = "/login")
	public ResponseEntity<ResponseResult> login (String username, String password) {
		UserBean user = userService.getUser(username);
		ResponseResult responseResult = new ResponseResult();

		if (user != null && password.equals(user.getPassword())) {
			responseResult.setMsg("登录成功");
			responseResult.setData(JwtUtil.generateToken(username, password));
			return new ResponseEntity<>(responseResult, HttpStatus.OK);
		} else {
			responseResult.setMsg("用户名和密码不对，请重试～");
			return new ResponseEntity<>(responseResult, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * 登录是否会返回不同的结果给用户
	 * @param request 请求
	 * @return 响应体
	 */
	@GetMapping(value = "/article")
	public ResponseEntity<ResponseResult> article(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		ResponseResult responseResult = new ResponseResult();

		if (subject.isAuthenticated()) {
			responseResult.setMsg("已经登录");
			responseResult.setData("你好 " + JwtUtil.getUserName(request.getHeader("Authorization")));
			return new ResponseEntity<>(responseResult, HttpStatus.OK);
		} else {
			responseResult.setMsg("未登录");
			responseResult.setData("你好，游客");
			return new ResponseEntity<>(responseResult, HttpStatus.OK);
		}
	}

	/**
	 * 必须要登录鉴权才能访问的接口
	 * @return 响应体
	 */
	@GetMapping(value = "/requireAuth")
	@RequiresAuthentication
	public ResponseEntity<ResponseResult> requireAuth() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("登录成功");
		responseResult.setData("Hello world～～～");
		return new ResponseEntity<>(responseResult, HttpStatus.OK);
	}

	@RequestMapping(path = "/401")
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ResponseResult> unauthorized() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("无权限，请先登录");
		return new ResponseEntity<>(responseResult, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * 需要角色鉴权后才能访问
	 * @return
	 */
	@GetMapping("/requireRole")
	@RequiresRoles("admin")
	public ResponseEntity<ResponseResult>  requireRole() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("登录成功并且角色鉴权成功");
		responseResult.setData("该用户该角色可以访问");
		return new ResponseEntity<>(responseResult, HttpStatus.OK);
	}

	@GetMapping("/requirePermission")
	@RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
	public ResponseEntity<ResponseResult> requirePermission() {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("权限设置");
		responseResult.setData("该用户该角色可以访问");
		return new ResponseEntity<>(responseResult, HttpStatus.OK);
	}

}
