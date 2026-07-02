-- ============================================
-- xxl-job - Task Scheduling Database
-- ============================================

CREATE DATABASE IF NOT EXISTS xxl_job DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xxl_job;

-- 1. xxl_job_info (任务信息表)
CREATE TABLE IF NOT EXISTS `xxl_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_cron` varchar(128) NOT NULL COMMENT '任务执行Cron表达式',
  `job_desc` varchar(255) NOT NULL COMMENT '任务描述',
  `add_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `author` varchar(64) NOT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `executor_route_strategy` varchar(64) DEFAULT 'FIRST' COMMENT '执行器路由策略',
  `executor_handler` varchar(255) NOT NULL COMMENT '执行器，任务Handler名称',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器参数',
  `executor_job_param` varchar(512) DEFAULT '' COMMENT '任务参数',
  `executor_fail_strategy` varchar(64) DEFAULT 'DO_EXEC' COMMENT '失败策略',
  `glue_type` tinyint(4) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT 'GLUE_0' COMMENT 'GLUE备注',
  `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID',
  `trigger_status` tinyint(4) DEFAULT '0' COMMENT '状态(0-禁用，1-运行)',
  `trigger_last_time` bigint(20) DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(20) DEFAULT '0' COMMENT '下次调度时间',
  `executor_run_time` bigint(20) DEFAULT '0' COMMENT '执行时长',
  `add_count` int(11) DEFAULT '0' COMMENT '调度成功次数',
  `run_count` int(11) DEFAULT '0' COMMENT '执行成功次数',
  `fail_count` int(11) DEFAULT '0' COMMENT '执行失败次数',
  `trigger_last_report_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB任务信息表';

-- 2. xxl_job_log (任务执行日志表)
CREATE TABLE IF NOT EXISTS `xxl_job_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务主键ID',
  `trigger_time` datetime NOT NULL COMMENT '调度时间',
  `trigger_code` int(11) NOT NULL COMMENT '调度结果：0-成功，1-失败',
  `trigger_msg` text COMMENT '调度日志内容',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器JobHandler名称',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器参数',
  `executor_route_strategy` varchar(64) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_queue_size` int(11) DEFAULT '0' COMMENT '执行器队列大小',
  `executor_run_time` bigint(20) DEFAULT '0' COMMENT '执行器运行时间：毫秒',
  `handle_code` int(11) NOT NULL COMMENT '执行结果：0-成功，1-失败，2-忽略',
  `handle_msg` text COMMENT '处理日志内容',
  `alarm_status` tinyint(4) DEFAULT '0' COMMENT '告警状态：0-无告警，1-告警成功',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `I_trigger_time` (`trigger_time`),
  KEY `I_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB调度日志表';

-- 3. xxl_job_logglue (GLUE日志)
CREATE TABLE IF NOT EXISTS `xxl_job_logglue` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务ID',
  `glue_type` tinyint(4) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
  `add_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB日志GLUE表';

-- 4. xxl_job_registry (执行器注册表)
CREATE TABLE IF NOT EXISTS `xxl_job_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `registry_group` varchar(255) NOT NULL COMMENT '注册组信息',
  `registry_key` varchar(255) NOT NULL COMMENT '注册键信息',
  `registry_value` varchar(255) NOT NULL COMMENT '注册值信息',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_REGISTRY_KEY` (`registry_group`, `registry_key`),
  KEY `I_UPDATE_TIME` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB执行器注册表';

-- 5. xxl_job_group (执行器管理表)
CREATE TABLE IF NOT EXISTS `xxl_job_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(64) NOT NULL COMMENT '执行器标题',
  `address_type` tinyint(4) NOT NULL COMMENT '地址类型：0-自动注册 1-手动录入',
  `channel_address` varchar(255) DEFAULT NULL COMMENT '自动注册的地址，手动录入的地址',
  `channel_port` int(11) DEFAULT NULL COMMENT '自动注册的端口',
  `timeout` int(11) DEFAULT 0 COMMENT '任务超时时间',
  `executor_log_path` varchar(255) DEFAULT NULL COMMENT '执行器日志文件路径',
  `executor_logretentiondays` int(11) DEFAULT 0 COMMENT '执行器日志文件保留天数',
  `executor_logname` varchar(64) DEFAULT NULL COMMENT '执行器日志文件名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_APP_NAME` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB执行器管理表';

-- 6. xxl_job_user (用户表)
CREATE TABLE IF NOT EXISTS `xxl_job_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码(加密)',
  `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户 1-管理员',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_USERNAME` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB用户表';

-- 7. xxl_job_log_report (调度报表)
CREATE TABLE IF NOT EXISTS `xxl_job_log_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `trigger_day` datetime DEFAULT NULL COMMENT '调度日期',
  `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中日志数量',
  `suc_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行成功数量',
  `fail_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行失败数量',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_TRIGGER_DAY` (`trigger_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB调度报表';

-- 8. xxl_job_lock (任务锁)
CREATE TABLE IF NOT EXISTS `xxl_job_lock` (
  `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB任务锁';

-- 初始化锁数据
INSERT IGNORE INTO xxl_job_lock (lock_name) VALUES
('schedule_lock');

-- 插入默认管理员账号 (密码: 123456)
INSERT IGNORE INTO xxl_job_user (username, password, role, permission, create_time, update_time) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL, NOW(), NOW());

SELECT 'xxl-job database initialization completed!' as message;
