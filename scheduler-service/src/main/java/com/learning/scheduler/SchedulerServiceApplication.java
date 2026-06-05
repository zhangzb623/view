package com.learning.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 调度服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.learning")
@EnableFeignClients(basePackages = "com.learning.scheduler.feign")
public class SchedulerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerServiceApplication.class, args);
        System.out.println("Scheduler Service started successfully!");
    }
}
