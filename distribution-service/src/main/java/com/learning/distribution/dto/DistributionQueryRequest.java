package com.learning.distribution.dto;

import lombok.Data;

@Data
public class DistributionQueryRequest {

    private Long distributorUserId;
    private Integer status;
    private Integer current = 1;
    private Integer size = 10;
}
