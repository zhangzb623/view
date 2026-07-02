-- ============================================
-- Spring Cloud Learning System - Database Init Script
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS learning_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learning_system;

-- ============================================
-- 用户数据库
-- ============================================
CREATE DATABASE IF NOT EXISTS user_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE user_db;

CREATE TABLE IF NOT EXISTS t_user_info (
    user_id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username         VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password         VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    phone            VARCHAR(20) COMMENT '手机号',
    email            VARCHAR(100) COMMENT '邮箱',
    avatar           VARCHAR(255) COMMENT '头像URL',
    balance          DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    status           TINYINT     DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE IF NOT EXISTS t_user_address (
    address_id       BIGINT      NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    user_id          BIGINT      NOT NULL COMMENT '用户ID',
    receiver_name    VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone   VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province         VARCHAR(50) NOT NULL COMMENT '省份',
    city             VARCHAR(50) NOT NULL COMMENT '城市',
    district         VARCHAR(50) NOT NULL COMMENT '区县',
    detail_address   VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default       TINYINT     DEFAULT 0 COMMENT '是否默认: 0否 1是',
    status           TINYINT     DEFAULT 1 COMMENT '状态: 0删除 1启用',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (address_id),
    KEY idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- ============================================
-- 商品数据库
-- ============================================
CREATE DATABASE IF NOT EXISTS product_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE product_db;

CREATE TABLE IF NOT EXISTS t_product (
    product_id       BIGINT      NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    category_id      INT         NOT NULL COMMENT '分类ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_desc     TEXT COMMENT '商品描述',
    product_image    VARCHAR(255) COMMENT '商品图片',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    stock            INT         NOT NULL DEFAULT 0 COMMENT '库存',
    status           TINYINT     DEFAULT 1 COMMENT '状态: 0下架 1上架 2删除',
    sales_count      INT         DEFAULT 0 COMMENT '销量',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    KEY idx_category (category_id),
    KEY idx_status (status),
    KEY idx_sales (sales_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE IF NOT EXISTS t_category (
    category_id      INT         NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    parent_id        INT         DEFAULT 0 COMMENT '父分类ID',
    category_name    VARCHAR(50) NOT NULL COMMENT '分类名称',
    level            TINYINT     DEFAULT 1 COMMENT '层级: 1-一级分类 2-二级分类 3-三级分类',
    sort_order       INT         DEFAULT 0 COMMENT '排序',
    status           TINYINT     DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (category_id),
    UNIQUE KEY uk_name (category_name, parent_id),
    INDEX idx_level (level),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================
-- 订单数据库 (ShardingJDBC分片)
-- ============================================
CREATE DATABASE IF NOT EXISTS order_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE order_db;

-- 分片表1 (order0)
CREATE TABLE IF NOT EXISTS t_order (
    order_id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户ID(分片键)',
    product_id       BIGINT      NOT NULL COMMENT '商品ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image    VARCHAR(255) COMMENT '商品图片',
    quantity         INT         NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price      DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待支付 1待发货 2待收货 3已完成 4已取消',
    pay_status       TINYINT     DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2已退款',
    pay_time         DATETIME    COMMENT '支付时间',
    address_id       BIGINT      COMMENT '收货地址ID',
    shipping_fee     DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    remark           VARCHAR(500) COMMENT '备注',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status),
    KEY idx_user_id_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表(分片表)';

-- 分片表2 (order1)
CREATE TABLE IF NOT EXISTS t_order (
    order_id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户ID(分片键)',
    product_id       BIGINT      NOT NULL COMMENT '商品ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image    VARCHAR(255) COMMENT '商品图片',
    quantity         INT         NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price      DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待支付 1待发货 2待收货 3已完成 4已取消',
    pay_status       TINYINT     DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2已退款',
    pay_time         DATETIME    COMMENT '支付时间',
    address_id       BIGINT      COMMENT '收货地址ID',
    shipping_fee     DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    remark           VARCHAR(500) COMMENT '备注',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status),
    KEY idx_user_id_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表(分片表)';

-- 分片表3 (order2)
CREATE TABLE IF NOT EXISTS t_order (
    order_id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户ID(分片键)',
    product_id       BIGINT      NOT NULL COMMENT '商品ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image    VARCHAR(255) COMMENT '商品图片',
    quantity         INT         NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price      DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待支付 1待发货 2待收货 3已完成 4已取消',
    pay_status       TINYINT     DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2已退款',
    pay_time         DATETIME    COMMENT '支付时间',
    address_id       BIGINT      COMMENT '收货地址ID',
    shipping_fee     DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    remark           VARCHAR(500) COMMENT '备注',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status),
    KEY idx_user_id_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表(分片表)';

-- 分片表4 (order3)
CREATE TABLE IF NOT EXISTS t_order (
    order_id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户ID(分片键)',
    product_id       BIGINT      NOT NULL COMMENT '商品ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image    VARCHAR(255) COMMENT '商品图片',
    quantity         INT         NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price      DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待支付 1待发货 2待收货 3已完成 4已取消',
    pay_status       TINYINT     DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2已退款',
    pay_time         DATETIME    COMMENT '支付时间',
    address_id       BIGINT      COMMENT '收货地址ID',
    shipping_fee     DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    remark           VARCHAR(500) COMMENT '备注',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    KEY idx_status (status),
    KEY idx_user_id_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表(分片表)';

-- ============================================
-- 支付数据库
-- ============================================
CREATE DATABASE IF NOT EXISTS payment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payment_db;

CREATE TABLE IF NOT EXISTS t_payment (
    payment_id       BIGINT      NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    payment_no       VARCHAR(32)  NOT NULL UNIQUE COMMENT '支付单号',
    order_id         BIGINT      NOT NULL COMMENT '订单ID',
    order_no         VARCHAR(32)  NOT NULL COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户ID',
    amount           DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method   VARCHAR(20)  NOT NULL COMMENT '支付方式: ALIPAY, WECHAT',
    trade_no         VARCHAR(64)  COMMENT '第三方交易号',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待支付 1成功 2失败 3退款',
    callback_content TEXT COMMENT '回调内容',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    pay_time         DATETIME    COMMENT '支付时间',
    PRIMARY KEY (payment_id),
    UNIQUE KEY uk_payment_no (payment_no),
    KEY idx_order_id (order_id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_trade_no (trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

CREATE TABLE IF NOT EXISTS t_refund (
    refund_id        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    refund_no        VARCHAR(32)  NOT NULL UNIQUE COMMENT '退款单号',
    payment_id       BIGINT      NOT NULL COMMENT '支付ID',
    order_id         BIGINT      NOT NULL COMMENT '订单ID',
    user_id          BIGINT      NOT NULL COMMENT '用户ID',
    refund_amount    DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_reason    VARCHAR(500) COMMENT '退款原因',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待处理 1成功 2失败',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (refund_id),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_payment_id (payment_id),
    KEY idx_order_id (order_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';

-- ============================================
-- 分销数据库
-- ============================================
CREATE DATABASE IF NOT EXISTS distribution_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE distribution_db;

CREATE TABLE IF NOT EXISTS t_distribution_record (
    distribution_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分销ID',
    order_id             BIGINT       NOT NULL COMMENT '订单ID',
    order_user_id        BIGINT       NOT NULL COMMENT '下单用户ID',
    distributor_user_id  BIGINT       NOT NULL COMMENT '分销用户ID',
    product_id           BIGINT       NOT NULL COMMENT '商品ID',
    order_amount         DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    commission_rate      DECIMAL(5,2)  NOT NULL COMMENT '佣金比例',
    commission_amount    DECIMAL(10,2) NOT NULL COMMENT '佣金金额',
    status               TINYINT      DEFAULT 0 COMMENT '状态: 0已计算 1已结算 2已取消',
    settled_time         DATETIME     COMMENT '结算时间',
    remark               VARCHAR(255) COMMENT '备注',
    create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted              TINYINT      DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (distribution_id),
    UNIQUE KEY uk_order_id (order_id),
    KEY idx_distributor_user_id (distributor_user_id),
    KEY idx_status (status),
    KEY idx_distributor_status (distributor_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销记录表';

-- ============================================
-- 插入测试数据
-- ============================================

USE user_db;

-- 用户数据
INSERT INTO t_user_info (username, password, phone, email, balance) VALUES
('admin', 'password123', '13800138000', 'admin@example.com', 10000.00),
('user1', 'password123', '13800138001', 'user1@example.com', 1000.00),
('user2', 'password123', '13800138002', 'user2@example.com', 500.00),
('user3', 'password123', '13800138003', 'user3@example.com', 2000.00);

-- 地址数据
INSERT INTO t_user_address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default, status) VALUES
(1, '张三', '13800138000', '广东省', '深圳市', '南山区', '科技园南区XX路XX号', 1, 1),
(2, '李四', '13800138001', '广东省', '深圳市', '福田区', 'XX路XX号', 1, 1);

USE product_db;

-- 商品数据
INSERT INTO t_category (category_id, parent_id, category_name, level, sort_order, status) VALUES
(1, 0, '手机数码', 1, 1, 1),
(2, 1, '智能手机', 2, 1, 1),
(3, 1, '笔记本', 2, 2, 1),
(4, 1, '平板电脑', 2, 3, 1),
(5, 0, '服装鞋帽', 1, 2, 1),
(6, 5, '男装', 2, 1, 1),
(7, 5, '女装', 2, 2, 1);

INSERT INTO t_product (category_id, product_name, product_desc, product_image, unit_price, stock, status) VALUES
(2, 'iPhone 15 Pro', 'A17 Pro芯片，钛金属设计', '/images/iphone15pro.jpg', 7999.00, 100, 1),
(2, 'iPhone 15', '出色、强大、超爱不释手', '/images/iphone15.jpg', 5999.00, 150, 1),
(3, 'MacBook Pro 16"', '强悍性能，专业创作利器', '/images/macbookpro16.jpg', 19999.00, 30, 1),
(3, 'MacBook Air M3', '轻薄便携，性能出色', '/images/macbookair.jpg', 8999.00, 50, 1),
(6, '运动休闲裤', '舒适透气，百搭款式', '/images/pants.jpg', 299.00, 200, 1),
(7, '时尚连衣裙', '优雅气质，连衣裙', '/images/dress.jpg', 499.00, 150, 1);

USE payment_db;

-- 支付数据
INSERT INTO t_payment (payment_no, order_id, order_no, user_id, amount, payment_method, status) VALUES
('PAY20240601001', 1, 'ORD20240601001', 1, 7999.00, 'ALIPAY', 1),
('PAY20240601002', 2, 'ORD20240601002', 2, 5999.00, 'WECHAT', 1),
('PAY20240601003', 3, 'ORD20240601003', 3, 19999.00, 'ALIPAY', 0);

-- 退款数据
INSERT INTO t_refund (refund_no, payment_id, order_id, user_id, refund_amount, status) VALUES
('REF20240601001', 1, 1, 1, 7999.00, 1),
('REF20240601002', 2, 2, 2, 5999.00, 0);

SELECT 'Database initialization completed!' as message;
