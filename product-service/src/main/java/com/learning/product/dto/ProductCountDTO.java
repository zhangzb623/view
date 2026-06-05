package com.learning.product.dto;

import lombok.Data;

/**
 * 商品数量分布DTO
 */
@Data
public class ProductCountDTO {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品数量
     */
    private Integer count;
}
