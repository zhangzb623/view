package com.learning.product.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新商品请求
 */
@Data
public class UpdateProductRequest {

    /**
     * 分类ID
     */
    private Long categoryId;

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
     * 状态
     */
    private Integer status;
}
