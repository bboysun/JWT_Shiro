package com.darryl.jwt.controller;

import com.darryl.jwt.model.ResponseResult;
import com.darryl.jwt.model.StudentModel;
import com.darryl.jwt.model.UserBean;
import com.darryl.jwt.service.IStudentService;
import com.darryl.jwt.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2020/07/05
 */
@RestController
public class AopLogController {

	@Autowired
	private IStudentService studentService;

	/**
	 * 查询
	 * @param id 用户名
	 * @return
	 */
	@PostMapping(value = "/query")
	public ResponseEntity<String> query (String id) {
		studentService.queryById(id);
		return new ResponseEntity<>("query OK", HttpStatus.OK);
	}

	/**
	 * 新增
	 * @return
	 */
	@PostMapping(value = "/save")
	public ResponseEntity<String> save () {
		StudentModel studentModel = new StudentModel();
		studentModel.setAge(11);
		studentModel.setId("1");
		studentModel.setGrade("grade");
		studentModel.setName("darryl");
		studentService.save(studentModel);
		return new ResponseEntity<>("add OK", HttpStatus.OK);
	}

}
