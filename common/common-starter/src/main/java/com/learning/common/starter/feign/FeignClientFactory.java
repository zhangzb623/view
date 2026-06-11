package com.learning.common.starter.feign;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        return create(clazz, getDefaultUrl(), null, null, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, Encoder encoder, Decoder decoder) {
        return create(clazz, getDefaultUrl(), encoder, decoder, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, String url) {
        return create(clazz, url, null, null, null);
    }

    /**
     * 创建Feign客户端
     */
    public static <T> T create(Class<T> clazz, String url, Encoder encoder, Decoder decoder) {
        return create(clazz, url, encoder, decoder, null);
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
        Feign.Builder builder = Feign.builder()
                .logger(new Slf4jLogger(clazz))
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(new FeignInterceptor())
                .retryer(new Retryer.Default(100, 1000, 3))
                .options(new Request.Options(3, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true));

        if (encoder != null) {
            builder.encoder(encoder);
        }
        if (decoder != null) {
            builder.decoder(decoder);
        }

        return builder.target(clazz, url);
    }

    /**
     * 获取默认URL
     */
    private static String getDefaultUrl() {
        return "http://localhost:8080";
    }

    private FeignClientFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }
}
