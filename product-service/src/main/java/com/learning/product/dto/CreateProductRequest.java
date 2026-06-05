package com.learning.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建商品请求
 */
@Data
public class CreateProductRequest {

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
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
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    private Integer stock;
}
