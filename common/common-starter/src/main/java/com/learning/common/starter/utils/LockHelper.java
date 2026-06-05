package com.learning.common.starter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 */
@Slf4j
@Component
public class LockHelper {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        return tryLock(key, timeout, unit, null);
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit, String requestId) {
        try {
            String lockKey = "lock:" + key;
            String lockValue = requestId != null ? requestId : UUID.randomUUID().toString();

            Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, unit);

            if (result != null && result) {
                log.debug("获取锁成功: key={}, requestId={}", lockKey, lockValue);
                return true;
            }

            log.debug("获取锁失败: key={}, requestId={}", lockKey, lockValue);
            return false;
        } catch (Exception e) {
            log.error("获取锁异常: key={}", key, e);
            return false;
        }
    }

    /**
     * 尝试获取锁（默认30秒过期时间）
     */
    public boolean tryLock(String key) {
        return tryLock(key, 30, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（默认30秒过期时间）
     */
    public boolean tryLock(String key, String requestId) {
        return tryLock(key, 30, TimeUnit.SECONDS, requestId);
    }

    /**
     * 释放锁
     */
    public boolean unlock(String key) {
        return unlock(key, null);
    }

    /**
     * 释放锁
     */
    public boolean unlock(String key, String requestId) {
        try {
            String lockKey = "lock:" + key;

            if (requestId != null) {
                // 释放指定requestId的锁
                String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
                if (requestId.equals(currentValue)) {
                    Boolean result = stringRedisTemplate.delete(lockKey);
                    log.debug("释放锁成功: key={}, requestId={}", lockKey, requestId);
                    return result != null && result;
                }
            } else {
                // 释放所有锁
                Boolean result = stringRedisTemplate.delete(lockKey);
                log.debug("释放锁成功: key={}", lockKey);
                return result != null && result;
            }

            log.warn("释放锁失败或锁不存在: key={}, requestId={}", lockKey, requestId);
            return false;
        } catch (Exception e) {
            log.error("释放锁异常: key={}, requestId={}", key, requestId, e);
            return false;
        }
    }

    /**
     * 检查锁是否存在
     */
    public boolean isLocked(String key) {
        String lockKey = "lock:" + key;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(lockKey));
    }

    /**
     * 设置锁的过期时间
     */
    public boolean expireLock(String key, long timeout, TimeUnit unit) {
        String lockKey = "lock:" + key;
        return Boolean.TRUE.equals(stringRedisTemplate.expire(lockKey, timeout, unit));
    }

    /**
     * 获取锁的剩余过期时间
     */
    public Long getLockExpire(String key, TimeUnit unit) {
        String lockKey = "lock:" + key;
        return stringRedisTemplate.getExpire(lockKey, unit);
    }

    /**
     * 自动获取锁和释放锁（try-finally模式）
     */
    public void executeWithLock(String key, long timeout, TimeUnit unit, Runnable callback) {
        String requestId = UUID.randomUUID().toString();
        boolean locked = tryLock(key, timeout, unit, requestId);
        try {
            if (locked) {
                callback.run();
            } else {
                log.warn("获取锁失败，跳过执行: key={}", key);
            }
        } finally {
            if (locked) {
                unlock(key, requestId);
            }
        }
    }

    /**
     * 自动获取锁和释放锁（默认30秒超时）
     */
    public void executeWithLock(String key, Runnable callback) {
        executeWithLock(key, 30, TimeUnit.SECONDS, callback);
    }

    private LockHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
