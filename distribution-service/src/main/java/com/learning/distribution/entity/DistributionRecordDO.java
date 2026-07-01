package com.learning.distribution.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.common.domain.BaseEntityDO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_distribution_record")
public class DistributionRecordDO extends BaseEntityDO {

    @TableId(value = "distribution_id", type = IdType.AUTO)
    private Long distributionId;

    private Long orderId;
    private Long orderUserId;
    private Long distributorUserId;
    private Long productId;
    private BigDecimal orderAmount;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private Integer status;
    private LocalDateTime settledTime;
    private String remark;
}
