package com.learning.common.starter.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 * 用于在非Spring管理的类中获取Spring Bean
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        log.info("Spring ApplicationContext 初始化完成");
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取Bean
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 获取Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取Bean
     */
    public static Object getBean(String name, Object... args) {
        return applicationContext.getBean(name, args);
    }

    /**
     * 获取Bean
     */
    public static <T> T getBean(Class<T> clazz, Object... args) {
        return applicationContext.getBean(clazz, args);
    }

    /**
     * 检查Bean是否存在
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 检查是否为单例Bean
     */
    public static boolean isSingleton(String name) throws BeansException {
        return applicationContext.isSingleton(name);
    }

    /**
     * 获取Bean类型
     */
    public static Class<?> getType(String name) throws BeansException {
        return applicationContext.getType(name);
    }

    private SpringContextHolder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
