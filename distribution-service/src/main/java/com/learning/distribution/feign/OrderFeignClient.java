package com.learning.distribution.feign;

import com.learning.common.api.result.Result;
import com.learning.distribution.feign.dto.OrderSnapshotDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", contextId = "distributionOrderFeignClient")
public interface OrderFeignClient {

    @GetMapping("/api/order/{orderId}")
    Result<OrderSnapshotDTO> getOrderById(@PathVariable("orderId") Long orderId);
}
