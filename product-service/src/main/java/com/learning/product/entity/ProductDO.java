package com.learning.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.common.domain.BaseEntityDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品实体类
 */
@Data
@TableName("t_product")
public class ProductDO extends BaseEntityDO {

    /**
     * 商品ID
     */
    @TableId
    private Long productId;

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
     * 状态: 0下架 1上架 2删除
     */
    private Integer status;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 是否删除: 0否 1是
     */
    private Integer deleted;
}
