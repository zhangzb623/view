package com.learning.scheduler.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobConfig {

    private Admin admin = new Admin();
    private Executor executor = new Executor();
    private String accessToken;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executorBean = new XxlJobSpringExecutor();
        executorBean.setAdminAddresses(admin.getAddresses());
        executorBean.setAppname(executor.getAppname());
        executorBean.setIp(executor.getIp());
        executorBean.setPort(executor.getPort());
        executorBean.setAccessToken(accessToken);
        executorBean.setLogPath(executor.getLogpath());
        executorBean.setLogRetentionDays(executor.getLogretentiondays());
        return executorBean;
    }

    @Data
    public static class Admin {
        private String addresses;
    }

    @Data
    public static class Executor {
        private String appname;
        private String ip;
        private int port;
        private String logpath;
        private int logretentiondays;
    }
}
