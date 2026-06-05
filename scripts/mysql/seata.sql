-- ============================================
-- Seata - Distributed Transaction Database
-- ============================================

CREATE DATABASE IF NOT EXISTS seata DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE seata;

-- 1. 全局事务表
CREATE TABLE IF NOT EXISTS `global_table` (
  `xid` varchar(128) NOT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `application_id` varchar(32) DEFAULT NULL,
  `transaction_service_group` varchar(32) DEFAULT NULL,
  `transaction_name` varchar(128) DEFAULT NULL,
  `timeout` int(11) DEFAULT NULL,
  `application_level` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `gaia_create_time` datetime DEFAULT NULL,
  `gaia_update_time` datetime DEFAULT NULL,
  `gaia_deleted` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`xid`),
  KEY `idx_gmt_create` (`create_time`),
  KEY `idx_gmt_modified` (`update_time`),
  KEY `idx_status` (`status`),
  KEY `idx_xid` (`xid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局事务表';

-- 2. 分支事务表
CREATE TABLE IF NOT EXISTS `branch_table` (
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(128) NOT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `resource_group_id` varchar(64) DEFAULT NULL,
  `resource_id` varchar(256) DEFAULT NULL,
  `branch_type` varchar(8) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `client_id` varchar(64) DEFAULT NULL,
  `application_data` varchar(256) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`branch_id`),
  KEY `idx_xid` (`xid`),
  KEY `idx_status` (`status`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分支事务表';

-- 3. 全局锁表
CREATE TABLE IF NOT EXISTS `lock_table` (
  `row_key` varchar(128) NOT NULL,
  `xid` varchar(96) DEFAULT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `branch_id` bigint(20) NOT NULL,
  `resource_id` varchar(256) DEFAULT NULL,
  `table_name` varchar(32) DEFAULT NULL,
  `pk` varchar(36) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`row_key`),
  KEY `idx_branch_id` (`branch_id`),
  KEY `idx_xid` (`xid`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局事务锁表';

-- 插入初始化数据
INSERT INTO `global_table` (`xid`, `status`, `transaction_service_group`, `create_time`) VALUES
('192.168.1.100:8091:1234567890', 1, 'learning-system-tx-group', NOW());

SELECT 'Seata database initialization completed!' as message;
