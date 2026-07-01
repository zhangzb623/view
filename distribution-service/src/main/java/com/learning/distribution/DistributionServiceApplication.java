package com.learning.distribution;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.learning")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.learning.distribution.feign")
@MapperScan("com.learning.distribution.mapper")
public class DistributionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributionServiceApplication.class, args);
    }
}
