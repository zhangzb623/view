package com.learning.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;

    /**
     * 商品数量
     */
    @NotNull(message = "商品数量不能为空")
    private Integer quantity;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    @NotNull(message = "总金额不能为空")
    private BigDecimal totalPrice;

    /**
     * 支付方式
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    private String address;

    /**
     * 收货人
     */
    @NotBlank(message = "收货人不能为空")
    private String receiver;

    /**
     * 收货电话
     */
    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone;

    /**
     * 备注
     */
    private String remark;
}
