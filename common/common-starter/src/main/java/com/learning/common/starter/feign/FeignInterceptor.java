package com.learning.common.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Feign请求拦截器
 * 用于在所有Feign请求中添加请求头
 */
@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void apply(RequestTemplate template) {
        // 获取请求头中的Authorization
        String token = template.header(AUTHORIZATION_HEADER);

        // 如果没有token，尝试从上下文中获取
        if (!StringUtils.hasText(token)) {
            token = getBearerToken();
        }

        // 如果有token，添加到请求头
        if (StringUtils.hasText(token)) {
            template.header(AUTHORIZATION_HEADER, token);
            log.debug("Feign request added Authorization header: {}", token.substring(0, Math.min(20, token.length())) + "...");
        }

        // 添加服务名称
        template.header("X-Service-Name", getServiceName());
    }

    /**
     * 从上下文中获取Token
     * 实际实现需要结合Spring Security或其他认证方案
     */
    private String getBearerToken() {
        // TODO: 实现从Spring Security上下文或其他地方获取Token的逻辑
        // 例如: return SecurityContextHolder.getContext().getAuthentication().getToken();
        return null;
    }

    /**
     * 获取当前服务名称
     */
    private String getServiceName() {
        // TODO: 实现获取当前服务名称的逻辑
        // 例如: return SpringContextHolder.getContext().getBean(Environment.class)
        //           .getProperty("spring.application.name");
        return "unknown";
    }
}
