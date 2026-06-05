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

    /**
     * 设置缓存
     */
    public <T> void set(String key, T value) {
        set(key, value, null);
    }

    /**
     * 设置缓存（带过期时间）
     */
    public <T> void set(String key, T value, Long timeout) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        if (timeout != null && timeout > 0) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置缓存（带过期时间）
     */
    public <T> void set(String key, T value, Long timeout, TimeUnit unit) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     */
    public <T> T get(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return (T) operations.get(key);
    }

    /**
     * 删除缓存
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, Long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间（秒）
     */
    public Boolean expire(String key, Long timeoutSeconds) {
        return expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取过期时间（秒）
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 判断键是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 查找匹配的key
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 增加值
     */
    public Long increment(String key) {
        return increment(key, 1);
    }

    /**
     * 增加值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 增加浮点数值
     */
    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 减少值
     */
    public Long decrement(String key) {
        return decrement(key, 1);
    }

    /**
     * 减少值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 获取哈希
     */
    public <T> Map<Object, T> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置哈希
     */
    public <T> void hSet(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取哈希值
     */
    public <T> T hGet(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除哈希
     */
    public Long hDel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断哈希key是否存在
     */
    public Boolean hHasKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取列表
     */
    public <T> List<T> lGet(String key) {
        return lGet(key, 0, -1);
    }

    /**
     * 获取列表
     */
    public <T> List<T> lGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表长度
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 将值插入列表
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将值插入列表
     */
    public Long lPush(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 获取列表指定索引的值
     */
    public <T> T lGet(String key, long index) {
        return (T) redisTemplate.opsForList().index(key, index);
    }

    /**
     * 从列表左侧弹出
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从列表右侧弹出
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    private CacheHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
