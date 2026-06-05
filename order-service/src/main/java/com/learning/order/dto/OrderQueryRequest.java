package com.learning.order.dto;

import lombok.Data;

/**
 * 订单查询请求
 */
@Data
public class OrderQueryRequest {

    private Long userId;
    private Integer status;
    private String username;
    private String productName;
    private Integer page = 1;
    private Integer size = 10;
}
