package com.learning.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础VO实体类
 * 所有视图对象都继承此类
 */
@Data
public class BaseEntityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
