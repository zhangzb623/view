package com.learning.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.common.domain.BaseEntityDO;
import lombok.Data;

/**
 * 用户地址实体类
 */
@Data
@TableName("t_user_address")
public class UserAddressDO extends BaseEntityDO {

    /**
     * 地址ID
     */
    @TableId
    private Long addressId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 是否默认: 0否 1是
     */
    private Integer isDefault;

    /**
     * 状态: 0删除 1启用
     */
    private Integer status;
}
