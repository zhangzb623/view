package com.learning.distribution.service;

import com.learning.common.api.result.PageResult;
import com.learning.distribution.dto.CreateDistributionRequest;
import com.learning.distribution.dto.DistributionDTO;
import com.learning.distribution.dto.DistributionQueryRequest;

public interface DistributionService {

    Long calculate(CreateDistributionRequest request);

    DistributionDTO getById(Long distributionId);

    DistributionDTO getByOrderId(Long orderId);

    PageResult<DistributionDTO> getByDistributor(DistributionQueryRequest request);

    void settle(Long distributionId, boolean simulateRollback);
}
