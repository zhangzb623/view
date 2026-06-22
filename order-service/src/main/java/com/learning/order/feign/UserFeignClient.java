package com.learning.order.feign;

import com.learning.common.api.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 */
@FeignClient(
    name = "user-service",
    contextId = "userFeignClient"
)
public interface UserFeignClient {

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/api/user/info/{userId}")
    Result<Object> getUserInfo(
        @PathVariable("userId") Long userId,
        @RequestHeader("Authorization") String token
    );

    /**
     * 扣减用户余额
     */
    @GetMapping("/api/user/balance/deduct")
    Result<Void> deductBalance(
        @RequestParam("userId") Long userId,
        @RequestParam("amount") java.math.BigDecimal amount,
        @RequestHeader("Authorization") String token
    );

    /**
     * 增加用户余额（退款用）
     */
    @GetMapping("/api/user/balance/add")
    Result<Void> addBalance(
        @RequestParam("userId") Long userId,
        @RequestParam("amount") java.math.BigDecimal amount,
        @RequestHeader("Authorization") String token
    );

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/api/user/username/{username}")
    Result<Object> getUserByUsername(
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String token
    );
}
