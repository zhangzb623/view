package com.learning.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 */
public class JsonUtils {

    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue);
    }

    /**
     * 对象转JSON字符串 (不写null)
     */
    public static String toJsonStringIgnoreNull(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String json, Type type) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, type);
    }

    /**
     * JSON字符串转对象列表
     */
    public static <T> List<T> parseList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * 对象转Map
     */
    public static <T> Map<String, Object> toMap(T obj) {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), Map.class);
    }

    /**
     * Map转对象
     */
    public static <T> T toObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
