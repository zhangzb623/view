package com.learning.distribution.feign;

import com.learning.common.api.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "user-service", contextId = "distributionUserFeignClient")
public interface UserFeignClient {

    @PostMapping("/api/user/balance/add")
    Result<Void> addBalance(@RequestParam("userId") Long userId,
                            @RequestParam("amount") BigDecimal amount);
}
