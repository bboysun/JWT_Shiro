package com.darryl.jwt.service.impl;

import com.alibaba.fastjson.JSON;
import com.darryl.jwt.annotation.log.annotation.LogEnable;
import com.darryl.jwt.annotation.log.annotation.LogEvent;
import com.darryl.jwt.annotation.log.annotation.LogKey;
import com.darryl.jwt.annotation.log.enums.EventType;
import com.darryl.jwt.annotation.log.enums.ModuleType;
import com.darryl.jwt.model.StudentModel;
import com.darryl.jwt.service.IStudentService;
import org.springframework.stereotype.Service;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2020/07/05
 */
@Service
@LogEnable() // 启动日志拦截
//@LogEvent(module = ModuleType.STUDENT)
public class StudentServiceImpl implements IStudentService {
	@Override
	@LogEvent(event = EventType.DELETE_SINGLE, desc = "删除记录") // 添加日志标识
	public void deleteById(@LogKey(keyName = "id") String id, String a) {
		System.out.printf(this.getClass() +  "deleteById  id = " + id);
	}

	@Override
	@LogEvent(event = EventType.ADD, desc = "保存记录") // 添加日志标识
	public int save(StudentModel studentModel) {
		System.out.printf(this.getClass() +  "save  save = " + JSON.toJSONString(studentModel));
		return 1;
	}

	@Override
	@LogEvent(event = EventType.UPDATE, desc = "更新记录") // 添加日志标识
	public void update(StudentModel studentModel) {
		System.out.printf(this.getClass() +  "save  update = " + JSON.toJSONString(studentModel));
	}

	// 没有日志标识
	@Override
	public void queryById(String id) {
		System.out.printf(this.getClass() +  "queryById  id = " + id);
	}
}
