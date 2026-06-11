package com.learning.common.starter.feign;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Feign配置
 */
@Configuration
public class FeignConfig {

    /**
     * Feign请求日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Feign重试配置
     */
    @Bean
    public Retryer feignRetryer() {
        // 最多重试3次，初始间隔100ms，最大间隔1000ms
        return new Retryer.Default(100, 1000, 3);
    }

    /**
     * Feign请求配置
     */
    @Bean
    public Request.Options feignRequestOptions() {
        // 连接超时3秒，读取超时5秒
        return new Request.Options(3, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true);
    }
}
