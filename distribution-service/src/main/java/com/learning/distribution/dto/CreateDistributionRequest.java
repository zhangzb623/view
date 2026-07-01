package com.learning.distribution.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDistributionRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "分销用户ID不能为空")
    private Long distributorUserId;

    @NotNull(message = "佣金比例不能为空")
    @DecimalMin(value = "0.01", message = "佣金比例必须大于0")
    private BigDecimal commissionRate;

    private String remark;
}
