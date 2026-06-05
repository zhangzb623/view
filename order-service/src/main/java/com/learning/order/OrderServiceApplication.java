package com.learning.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.learning")
@MapperScan("com.learning.order.mapper")
@EnableFeignClients(basePackages = "com.learning.order.feign")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("Order Service started successfully!");
    }
}
