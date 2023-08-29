package com.darryl.jwt.license;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: Darryl
 * @Description: license check interceptor
 * @Date: 2023/08/29
 */
@Component
public class LicenseCheckInterceptor {

	private LicenseCheckTask task = new LicenseCheckTask("darrylsun_test");

	private ScheduledExecutorService scheduledExecutorService;

	@PostConstruct
	public void init() {
		ThreadFactory threadFactory = new CustomizableThreadFactory("license-check-pool-");
		scheduledExecutorService = new ScheduledThreadPoolExecutor(1, threadFactory);
		scheduledExecutorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
	}

	public Boolean getLicenseCheckRes() {
		return task.getCheckRes();
	}

}
