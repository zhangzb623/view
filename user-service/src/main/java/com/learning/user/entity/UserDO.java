package com.learning.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.common.domain.BaseEntityDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户实体类
 */
@Data
@TableName("t_user_info")
public class UserDO extends BaseEntityDO {

    /**
     * 用户ID
     */
    @TableId
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码(加密)
     */
    private String password;

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

    /**
     * 是否删除: 0否 1是
     */
    private Integer deleted;
}
