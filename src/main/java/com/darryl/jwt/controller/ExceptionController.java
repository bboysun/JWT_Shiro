package com.darryl.jwt.controller;

import com.darryl.jwt.model.ResponseResult;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: Darryl
 * @Description: 做一个controller的AOP，用于捕获shiro的异常
 * @Date: 2020/06/21
 */
@RestControllerAdvice
public class ExceptionController {

	// 捕捉shiro的异常
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(ShiroException.class)
	public ResponseEntity<ResponseResult> handle401(ShiroException e) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("Unauthorized");
		responseResult.setData(e.getMessage());
		return new ResponseEntity(responseResult, HttpStatus.UNAUTHORIZED);
	}

	// 捕捉其他所有异常
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> globalException(HttpServletRequest request, Throwable ex) {
		ResponseResult responseResult = new ResponseResult();
		responseResult.setMsg("Unauthorized");
		responseResult.setData(ex.getMessage());
		return new ResponseEntity(responseResult, getStatus(request));
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return HttpStatus.valueOf(statusCode);
	}
}
