package com.learning.common.util;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 去除字符串首尾空格
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 判断字符串是否包含
     */
    public static boolean contains(String str, char charToCheck) {
        return str != null && str.indexOf(charToCheck) >= 0;
    }

    /**
     * 判断字符串是否包含
     */
    public static boolean contains(String str, String substring) {
        return str != null && substring != null && str.contains(substring);
    }

    /**
     * 截取字符串
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        return str.substring(start, end);
    }

    /**
     * 截取字符串
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            return "";
        }
        if (start >= str.length()) {
            return str;
        }
        return str.substring(start);
    }

    /**
     * 截取字符串 (默认截取到指定长度)
     */
    public static String substring(String str, int start, int length, String suffix) {
        if (str == null) {
            return null;
        }
        int end = start + length;
        String result = substring(str, start, end);
        if (isNotEmpty(result) && result.length() > length) {
            result = result.substring(0, length) + suffix;
        }
        return result;
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 重复字符串
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 连接字符串
     */
    public static String join(String delimiter, String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    /**
     * 填充字符串
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        return StringUtils.repeat(String.valueOf(padChar), length - str.length()) + str;
    }

    /**
     * 填充字符串
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= length) {
            return str;
        }
        return str + StringUtils.repeat(String.valueOf(padChar), length - str.length());
    }

    /**
     * 将字符串数组转为逗号分隔的字符串
     */
    public static String joinByComma(String... strings) {
        return join(",", strings);
    }

    /**
     * 将字符串数组转为空格分隔的字符串
     */
    public static String joinBySpace(String... strings) {
        return join(" ", strings);
    }

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
