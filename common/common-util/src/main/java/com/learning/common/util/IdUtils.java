package com.learning.common.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ID生成工具类
 */
public class IdUtils {

    private static final long START_TIMESTAMP = System.currentTimeMillis();
    private static final long SEQUENCE_BITS = 12L;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 生成雪花ID
     */
    public static long generateSnowflakeId() {
        long timestamp = System.currentTimeMillis();
        long sequence = (timestamp - START_TIMESTAMP) & SEQUENCE_MASK;
        return (timestamp << SEQUENCE_BITS) | sequence;
    }

    /**
     * 生成短ID (用于订单号)
     */
    public static String generateShortId() {
        return System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(1000));
    }

    /**
     * 生成订单号
     */
    public static String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    /**
     * 生成交易号
     */
    public static String generateTransactionNo() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    /**
     * 生成支付号
     */
    public static String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    /**
     * 生成消息ID
     */
    public static String generateMessageId() {
        return "MSG" + System.currentTimeMillis() + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    /**
     * 生成用户ID
     */
    public static Long generateUserId() {
        return generateSnowflakeId();
    }

    /**
     * 生成商品ID
     */
    public static Long generateProductId() {
        return generateSnowflakeId();
    }

    private IdUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
