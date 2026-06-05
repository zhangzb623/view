package com.learning.common.starter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI learningSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Cloud Learning System API")
                        .description("Comprehensive Spring Cloud microservices learning platform demonstrating all major technologies")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Zhang Zebiao")
                                .email("zzb649757708@163.com")
                                .url("https://github.com/zhangzebiao"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
