package com.learning.common.starter.feign;

import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Feign客户端工厂
 * 用于创建Feign客户端实例
 */
@Slf4j
public class FeignClientFactory {

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz) {
        return create(clazz, null, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, Encoder encoder, Decoder decoder) {
        return Feign.builder()
                .target(clazz, getDefaultUrl())
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Slf4jLogger(clazz))
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(new FeignInterceptor())
                .retryer(new Retryer.Default(100, 1000, 3))
                .options(new Request.Options(3, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true))
                .build();
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, String url) {
        return create(clazz, url, null, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, String url, Encoder encoder, Decoder decoder) {
        return Feign.builder()
                .target(clazz, url)
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Slf4jLogger(clazz))
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(new FeignInterceptor())
                .retryer(new Retryer.Default(100, 1000, 3))
                .options(new Request.Options(3, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true))
                .build();
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, String url, Map<String, Object> options) {
        return create(clazz, url, null, null, options);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, String url, Encoder encoder, Decoder decoder,
                               Map<String, Object> options) {
        return Feign.builder()
                .target(clazz, url)
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Slf4jLogger(clazz))
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(new FeignInterceptor())
                .retryer(new Retryer.Default(100, 1000, 3))
                .options(new Request.Options(3, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true))
                .build();
    }

    /**
     * 获取默认URL
     */
    private static String getDefaultUrl() {
        // TODO: 从配置文件中获取
        return "http://localhost:8080";
    }

    private FeignClientFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }
}
