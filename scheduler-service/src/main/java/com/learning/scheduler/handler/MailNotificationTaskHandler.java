package com.learning.scheduler.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 邮件通知任务处理器
 */
@Slf4j
@Component
public class MailNotificationTaskHandler {

    /**
     * 邮件发送任务
     */
    @XxlJob("sendDailyReportJob")
    public void sendDailyReportJob() {
        log.info("开始执行邮件发送任务...");

        try {
            // 模拟获取需要发送邮件的用户列表
            List<Map<String, Object>> users = getPendingUsers();

            log.info("获取到 {} 个待发送邮件用户", users.size());

            int successCount = 0;
            int failCount = 0;

            for (Map<String, Object> user : users) {
                try {
                    // 模拟发送邮件
                    String email = (String) user.get("email");
                    String name = (String) user.get("name");

                    log.info("发送邮件给: {} ({}), 主题: 日常报告", name, email);
                    sendEmail(email, name);

                    successCount++;

                } catch (Exception e) {
                    log.error("发送邮件失败: {}", user, e);
                    failCount++;
                }
            }

            String result = String.format("邮件发送完成，成功 %d 封，失败 %d 封", successCount, failCount);
            XxlJobHelper.handleSuccess(result);
            log.info(result);

        } catch (Exception e) {
            log.error("邮件发送任务失败", e);
            XxlJobHelper.handleFail("邮件发送任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取待发送邮件的用户列表
     */
    private List<Map<String, Object>> getPendingUsers() {
        List<Map<String, Object>> users = new ArrayList<>();

        // 模拟数据
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "张三");
        user1.put("email", "zhangsan@example.com");
        users.add(user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "李四");
        user2.put("email", "lisi@example.com");
        users.add(user2);

        return users;
    }

    /**
     * 模拟发送邮件
     */
    private void sendEmail(String email, String name) {
        log.info("模拟发送邮件: To={}, Name={}, Subject: 日常数据报告", email, name);
    }
}
