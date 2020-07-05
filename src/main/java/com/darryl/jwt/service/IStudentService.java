package com.darryl.jwt.service;

import com.darryl.jwt.model.StudentModel;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2020/07/05
 */
public interface IStudentService {
	void deleteById(String id, String a);

	int save(StudentModel studentModel);

	void update(StudentModel studentModel);

	void queryById(String id);

}
