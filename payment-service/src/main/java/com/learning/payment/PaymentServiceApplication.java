package com.learning.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 支付服务启动类 - Seata分布式事务
 */
@SpringBootApplication(scanBasePackages = "com.learning")
@MapperScan("com.learning.payment.mapper")
@EnableFeignClients(basePackages = "com.learning.payment.feign")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("Payment Service started successfully!");
    }
}
