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
        // 可以在这里添加重试逻辑或发送告警
    }

    /**
     * 处理运行时异常
     */
    private void handleRuntimeException(RuntimeException e) {
        log.error("Feign调用失败: {}", e.getMessage(), e);
        // 可以根据具体的异常类型进行不同的处理
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
        if (e instanceof RetryableException) {
            return true;
        }
        // 可以根据业务规则判断是否需要重试
        return false;
    }

    /**
     * 获取异常重试次数
     */
    public int getRetryCount(Exception e) {
        if (e instanceof RetryableException) {
            RetryableException retryableException = (RetryableException) e;
            return retryableException.retryCount();
        }
        return 0;
    }

    private FeignExceptionHandler() {
        throw new UnsupportedOperationException("Handler class cannot be instantiated");
    }
}
