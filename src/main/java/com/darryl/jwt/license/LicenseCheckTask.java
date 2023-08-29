package com.darryl.jwt.license;

import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: Darryl
 * @Description: license check task
 * @Date: 2023/08/29
 */
@Slf4j
public class LicenseCheckTask implements Runnable {

	private String taskName;

	private static volatile boolean checkRes;

	public LicenseCheckTask(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public void run() {
		log.info("【开始】执行 license 本地校验");
		checkRes = true;
		log.info("【结束】执行 license 本地校验");
	}

	public Boolean getCheckRes() {
		return checkRes;
	}
}
