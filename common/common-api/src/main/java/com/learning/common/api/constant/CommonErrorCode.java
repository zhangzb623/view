package com.learning.common.api.constant;

import com.learning.common.api.result.ResultCode;

/**
 * 通用错误码枚举
 */
public enum CommonErrorCode {

    SUCCESS(ResultCode.SUCCESS),
    ERROR(ResultCode.ERROR),

    // 400系列
    BAD_REQUEST(ResultCode.BAD_REQUEST),
    PARAM_ERROR(ResultCode.VALIDATE_FAILED, "参数错误"),
    MISSING_PARAM(ResultCode.VALIDATE_FAILED, "缺少必要参数"),
    INVALID_PARAM(ResultCode.VALIDATE_FAILED, "参数格式错误"),

    // 401系列
    UNAUTHORIZED(ResultCode.UNAUTHORIZED, "未授权"),
    TOKEN_EXPIRED(ResultCode.UNAUTHORIZED, "Token已过期"),
    TOKEN_INVALID(ResultCode.UNAUTHORIZED, "Token无效"),

    // 403系列
    FORBIDDEN(ResultCode.FORBIDDEN, "禁止访问"),
    ACCESS_DENIED(ResultCode.FORBIDDEN, "无权限访问"),

    // 404系列
    NOT_FOUND(ResultCode.NOT_FOUND, "资源不存在"),
    RESOURCE_NOT_FOUND(ResultCode.NOT_FOUND, "资源不存在"),

    // 500系列
    INTERNAL_SERVER_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "服务器内部错误"),
    SYSTEM_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "系统错误"),
    SERVICE_UNAVAILABLE(ResultCode.SERVICE_UNAVAILABLE, "服务不可用"),

    // 业务错误
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    PASSWORD_ERROR(10003, "密码错误"),
    ACCOUNT_LOCKED(10004, "账号已锁定"),
    ACCOUNT_DISABLED(10005, "账号已禁用"),

    PRODUCT_NOT_FOUND(20001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(20002, "商品库存不足"),
    PRODUCT_DISCONTINUED(20003, "商品已下架"),

    ORDER_NOT_FOUND(30001, "订单不存在"),
    ORDER_STATUS_ERROR(30002, "订单状态错误"),
    ORDER_PAY_FAILED(30003, "订单支付失败"),

    BALANCE_NOT_ENOUGH(40001, "余额不足"),

    // 数据库错误
    DATABASE_ERROR(50001, "数据库错误"),
    DUPLICATE_KEY_ERROR(50002, "数据重复"),
    DATA_NOT_FOUND(50003, "数据不存在");

    private final Integer code;
    private final String message;

    CommonErrorCode(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    CommonErrorCode(ResultCode resultCode, String message) {
        this.code = resultCode.getCode();
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
