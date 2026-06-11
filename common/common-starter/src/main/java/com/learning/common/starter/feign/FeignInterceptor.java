package com.learning.common.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * Feign请求拦截器
 * 用于在所有Feign请求中添加请求头
 */
@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        String token = resolveAuthorizationHeader(template);

        if (StringUtils.hasText(token)) {
            template.header(AUTHORIZATION_HEADER, token);
            log.debug("Feign request added Authorization header: {}", token.substring(0, Math.min(20, token.length())) + "...");
        }

        template.header("X-Service-Name", getServiceName());
    }

    private String resolveAuthorizationHeader(RequestTemplate template) {
        Collection<String> values = template.headers().get(AUTHORIZATION_HEADER);
        if (values != null) {
            for (String value : values) {
                if (StringUtils.hasText(value)) {
                    return value;
                }
            }
        }
        return getBearerToken();
    }

    private String getBearerToken() {
        return null;
    }

    private String getServiceName() {
        return "unknown";
    }
}
