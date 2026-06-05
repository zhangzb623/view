package com.learning.product.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类DTO
 */
@Data
public class CategoryDTO {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 层级: 1-一级分类 2-二级分类 3-三级分类
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
