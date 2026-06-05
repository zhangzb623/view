package com.learning.user.dto;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {

    /**
     * Token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;
}
