package com.darryl.jwt.annotation.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.darryl.jwt.annotation.log.model.LogAdmModel;
import com.darryl.jwt.annotation.log.service.ILogManager;
import org.springframework.stereotype.Service;

/**
 * @Auther: Darryl
 * @Description: ILogManager实现类，将日志入库。这里只模拟入库
 * @Date: 2020/07/05
 */
@Service
public class DBLogManager implements ILogManager {

	@Override
	public void dealLog(LogAdmModel paramLogAdmBean) {
		System.out.println("将日志存入数据库,日志内容如下: " + JSON.toJSONString(paramLogAdmBean));
	}
}
