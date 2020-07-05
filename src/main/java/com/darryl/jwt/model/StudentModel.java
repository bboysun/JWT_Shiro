package com.darryl.jwt.model;

import com.darryl.jwt.annotation.log.annotation.LogKey;
import lombok.Data;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2020/07/05
 */
@Data
public class StudentModel {
	@LogKey(isUserId = true)
	private String id; // 编号
	private String name;  // 名称
	private Integer age; // 年龄
	private String grade;  // 年级
}
