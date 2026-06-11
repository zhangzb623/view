package com.learning.common.starter.feign;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.slf4j.Slf4jLogger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Feign客户端基础类
 * 提供通用的Feign客户端创建逻辑
 */
public final class BaseFeignClient {

    /**
     * 创建Feign客户端
     */
    public static <T> T createFeignClient(Class<T> apiClass) {
        return createFeignClient(apiClass, getDefaultUrl(), null, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T createFeignClient(Class<T> apiClass, String url) {
        return createFeignClient(apiClass, url, null, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T createFeignClient(Class<T> apiClass, String url, Encoder encoder, Decoder decoder) {
        return createFeignClient(apiClass, url, null, encoder, decoder);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T createFeignClient(Class<T> apiClass, String url, Map<String, Object> options,
                                          Encoder encoder, Decoder decoder) {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Slf4jLogger(apiClass))
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(new FeignInterceptor())
                .retryer(new Retryer.Default(100, 1000, 3))
                .options(new Request.Options(3, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true))
                .target(apiClass, url);
    }

    /**
     * 获取默认URL
     */
    private static String getDefaultUrl() {
        return "http://localhost:8080";
    }

    private BaseFeignClient() {
    }
}
