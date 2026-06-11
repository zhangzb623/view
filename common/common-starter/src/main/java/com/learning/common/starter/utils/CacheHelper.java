package com.learning.common.starter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类
 */
@Slf4j
@Component
public class CacheHelper {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T> void set(String key, T value) {
        set(key, value, null);
    }

    public <T> void set(String key, T value, Long timeout) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        if (timeout != null && timeout > 0) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
    }

    public <T> void set(String key, T value, Long timeout, TimeUnit unit) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeout, unit);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return (T) operations.get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    public Boolean expire(String key, Long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    public Boolean expire(String key, Long timeoutSeconds) {
        return expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Long increment(String key) {
        return increment(key, 1);
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key) {
        return decrement(key, 1);
    }

    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    @SuppressWarnings("unchecked")
    public <T> Map<Object, T> hGetAll(String key) {
        return (Map<Object, T>) (Map<?, ?>) redisTemplate.opsForHash().entries(key);
    }

    public <T> void hSet(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    public Long hDel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    public Boolean hHasKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public <T> List<T> lGet(String key) {
        return lGet(key, 0, -1);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> lGet(String key, long start, long end) {
        return (List<T>) (List<?>) redisTemplate.opsForList().range(key, start, end);
    }

    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public Long lPush(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @SuppressWarnings("unchecked")
    public <T> T lGet(String key, long index) {
        return (T) redisTemplate.opsForList().index(key, index);
    }

    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
}
