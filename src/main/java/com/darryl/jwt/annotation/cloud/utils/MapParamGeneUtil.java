package com.darryl.jwt.annotation.cloud.utils;

import com.alibaba.fastjson.JSON;
import com.darryl.jwt.annotation.cloud.serializers.CloudFormSerializer;
import com.darryl.jwt.model.StudentModel;
import com.darryl.jwt.service.UserService;
import com.google.common.collect.Maps;
import org.apache.catalina.User;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Auther: Darryl
 * @Description:
 * @Date: 2021/03/28
 */
public class MapParamGeneUtil {

	public static <T> Map<String, String> generateMapParam(T t) throws IllegalAccessException {
		Map<String, String> param = Maps.newHashMap();
		Field[] fields = t.getClass().getDeclaredFields();
		for (int i=0; i<fields.length; i++) {
			fields[i].setAccessible(true);
			String fieldName = fields[i].getName();
			String fieldValue = null;
			if (fields[i].get(t) != null) {
				Object f = fields[i].get(t);
				fieldValue = f instanceof String ? (String) f : JSON.toJSONString(f);
			}
			if (fieldValue != null) {
				param.put(fieldName, fieldValue);
			}
		}
		return param;
	}

	public static void main(String[] args) throws IllegalAccessException {
		StudentModel studentModel = new StudentModel();
		studentModel.setName("darryl");
		studentModel.setAge(18);
		Map<String, String> paramMap = generateMapParam(studentModel);
		paramMap.forEach((key, value)->{
			System.out.println("key is " + key + " value is " + value);
		});

		CloudFormSerializer cloudFormSerializer = new CloudFormSerializer();
		String serialize = cloudFormSerializer.serialize(studentModel);
		System.out.println(serialize);
	}
}
