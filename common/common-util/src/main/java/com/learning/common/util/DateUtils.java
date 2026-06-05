package com.learning.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 当前日期
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 当前时间戳
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * LocalDateTime转String
     */
    public static String toString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * LocalDateTime转String (自定义格式)
     */
    public static String toString(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * String转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }

    /**
     * String转LocalDateTime (自定义格式)
     */
    public static LocalDateTime toLocalDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * 获取当前日期时间
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 解析日期
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 获取当月第一天
     */
    public static LocalDate getFirstDayOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    /**
     * 获取当月最后一天
     */
    public static LocalDate getLastDayOfMonth() {
        return LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1);
    }

    /**
     * 获取下月第一天
     */
    public static LocalDate getFirstDayOfNextMonth() {
        return LocalDate.now().plusMonths(1).withDayOfMonth(1);
    }

    /**
     * 获取当年第一天
     */
    public static LocalDate getFirstDayOfYear() {
        return LocalDate.now().withMonth(1).withDayOfMonth(1);
    }

    /**
     * 获取当年最后一天
     */
    public static LocalDate getLastDayOfYear() {
        return LocalDate.now().withMonth(12).withDayOfMonth(31);
    }

    /**
     * 获取年份
     */
    public static int getYear(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.getYear();
    }

    /**
     * 获取月份
     */
    public static int getMonthValue(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.getMonthValue();
    }

    /**
     * 获取日
     */
    public static int getDayOfMonth(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.getDayOfMonth();
    }

    /**
     * 增加天数
     */
    public static LocalDate plusDays(LocalDate date, int days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    /**
     * 增加月数
     */
    public static LocalDate plusMonths(LocalDate date, int months) {
        if (date == null) {
            return null;
        }
        return date.plusMonths(months);
    }

    /**
     * 增加年数
     */
    public static LocalDate plusYears(LocalDate date, int years) {
        if (date == null) {
            return null;
        }
        return date.plusYears(years);
    }

    /**
     * 减少天数
     */
    public static LocalDate minusDays(LocalDate date, int days) {
        if (date == null) {
            return null;
        }
        return date.minusDays(days);
    }

    /**
     * 获取两个日期之间的天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }

    /**
     * 获取两个日期之间的月数差
     */
    public static long monthsBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return Period.between(start, end).getMonths();
    }

    private DateUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
