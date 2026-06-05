package com.learning.message;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 消息服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.learning")
@MapperScan("com.learning.message.mapper")
@EnableFeignClients(basePackages = "com.learning.message.feign")
public class MessageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
        System.out.println("Message Service started successfully!");
    }
}
