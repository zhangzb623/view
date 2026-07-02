package com.learning.scheduler.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试任务处理器
 */
@Slf4j
@Component
public class TestTaskHandler {

    /**
     * 测试定时任务
     */
    @XxlJob("testJob")
    public void testJob() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("任务执行: jobId={}, jobParam={}", XxlJobHelper.getJobId(), jobParam);

        try {
            // 模拟任务执行
            log.info("开始执行测试任务...");

            // 获取当前时间
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 构建执行结果
            Map<String, Object> result = new HashMap<>();
            result.put("currentTime", currentTime);
            result.put("status", "success");
            result.put("message", "任务执行成功");

            // 返回执行结果
            XxlJobHelper.handleSuccess(result.toString());

            log.info("任务执行完成: {}", result);

        } catch (Exception e) {
            log.error("任务执行失败", e);
            // 返回执行失败
            XxlJobHelper.handleFail("任务执行失败: " + e.getMessage());
        }
    }

    /**
     * 数据统计任务
     */
    @XxlJob("statisticsJob")
    public void statisticsJob() {
        log.info("开始执行数据统计任务...");

        try {
            // 模拟数据统计
            Map<String, Object> result = new HashMap<>();
            result.put("userCount", 12580);
            result.put("orderCount", 8900);
            result.put("paymentCount", 8750);
            result.put("statisticsTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            XxlJobHelper.handleSuccess(result.toString());
            log.info("数据统计完成: {}", result);

        } catch (Exception e) {
            log.error("数据统计失败", e);
            XxlJobHelper.handleFail("数据统计失败: " + e.getMessage());
        }
    }

    /**
     * 订单超时检查任务
     */
    @XxlJob("orderTimeoutJob")
    public void orderTimeoutJob() {
        log.info("开始执行订单超时检查任务...");

        try {
            // 查询待支付订单（模拟）
            log.info("查询待支付订单...");

            // 更新超时订单状态
            log.info("更新超时订单状态...");

            XxlJobHelper.handleSuccess("订单超时检查完成，共处理 " + 15 + " 条超时订单");
            log.info("订单超时检查完成");

        } catch (Exception e) {
            log.error("订单超时检查失败", e);
            XxlJobHelper.handleFail("订单超时检查失败: " + e.getMessage());
        }
    }

    /**
     * 缓存清理任务
     */
    @XxlJob("cacheCleanupJob")
    public void cacheCleanupJob() {
        log.info("开始执行缓存清理任务...");

        try {
            // 清理过期缓存（模拟）
            int cleanedCount = 120;

            XxlJobHelper.handleSuccess("缓存清理完成，共清理 " + cleanedCount + " 条缓存数据");
            log.info("缓存清理完成，共清理 {} 条缓存数据", cleanedCount);

        } catch (Exception e) {
            log.error("缓存清理失败", e);
            XxlJobHelper.handleFail("缓存清理失败: " + e.getMessage());
        }
    }

    /**
     * 报表生成任务
     */
    @XxlJob("reportJob")
    public void reportJob() {
        log.info("开始执行报表生成任务...");

        try {
            // 模拟生成报表
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            reportData.put("sales", 56800.50);
            reportData.put("orders", 892);
            reportData.put("users", 125);

            XxlJobHelper.handleSuccess(reportData.toString());
            log.info("报表生成完成: {}", reportData);

        } catch (Exception e) {
            log.error("报表生成失败", e);
            XxlJobHelper.handleFail("报表生成失败: " + e.getMessage());
        }
    }
}
