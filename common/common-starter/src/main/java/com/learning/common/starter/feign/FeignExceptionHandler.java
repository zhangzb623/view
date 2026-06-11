package com.learning.common.starter.feign;

import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign客户端异常处理器
 * 处理Feign调用中的各种异常
 */
@Slf4j
@Component
public class FeignExceptionHandler {

    /**
     * 处理Feign异常
     */
    public void handleException(Exception e) {
        if (e instanceof RetryableException) {
            handleRetryableException((RetryableException) e);
        } else if (e instanceof RuntimeException) {
            handleRuntimeException((RuntimeException) e);
        } else {
            handleOtherException(e);
        }
    }

    /**
     * 处理可重试异常
     */
    private void handleRetryableException(RetryableException e) {
        log.warn("Feign调用重试中: {}", e.getMessage(), e);
    }

    /**
     * 处理运行时异常
     */
    private void handleRuntimeException(RuntimeException e) {
        log.error("Feign调用失败: {}", e.getMessage(), e);
    }

    /**
     * 处理其他异常
     */
    private void handleOtherException(Exception e) {
        log.error("Feign调用异常: {}", e.getMessage(), e);
    }

    /**
     * 检查是否需要重试
     */
    public boolean shouldRetry(Exception e) {
        return e instanceof RetryableException;
    }

    /**
     * 获取异常重试次数
     */
    public int getRetryCount(Exception e) {
        return e instanceof RetryableException ? 1 : 0;
    }
}
