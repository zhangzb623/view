package com.learning.distribution.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DistributionDTO {

    private Long distributionId;
    private Long orderId;
    private Long orderUserId;
    private Long distributorUserId;
    private Long productId;
    private BigDecimal orderAmount;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private Integer status;
    private String statusText;
    private LocalDateTime settledTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
