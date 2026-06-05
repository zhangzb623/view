package com.learning.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品DTO
 */
@Data
public class ProductDTO {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品描述
     */
    private String productDesc;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 状态: 0下架 1上架 2删除
     */
    private Integer status;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
