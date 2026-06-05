package com.learning.user.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户DTO
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;
}
