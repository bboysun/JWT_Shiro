package com.darryl.jwt.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Darryl
 * @Description: 因为DB这次不是重点，我们模拟DB
 * @Date: 2020/06/21
 */
public class DataSource {
	private static Map<String, Map<String, String>> data = new HashMap<>();

	static {
		Map<String, String> data1 = new HashMap<>();
		data1.put("password", "darryl123");
		data1.put("role", "user");
		data1.put("permission", "view");

		Map<String, String> data2 = new HashMap<>();
		data2.put("password", "marisa123");
		data2.put("role", "admin");
		data2.put("permission", "view,edit");

		data.put("darryl", data1);
		data.put("marisa", data2);
	}

	public static Map<String, Map<String, String>> getData() {
		return data;
	}
}
