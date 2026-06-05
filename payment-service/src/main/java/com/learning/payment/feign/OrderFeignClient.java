package com.learning.payment.feign;

import com.learning.common.api.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务Feign客户端
 */
@FeignClient(
    name = "order-service",
    contextId = "orderFeignClient"
)
public interface OrderFeignClient {

    /**
     * 根据订单ID查询订单
     */
    @GetMapping("/api/order/{orderId}")
    Result<Object> getOrderById(
        @PathVariable("orderId") Long orderId,
        @RequestHeader("Authorization") String token
    );

    /**
     * 支付订单
     */
    @PostMapping("/api/order/{orderId}/pay")
    Result<Void> payOrder(
        @PathVariable("orderId") Long orderId,
        @RequestParam String transactionId,
        @RequestParam Integer paymentMethod,
        @RequestHeader("Authorization") String token
    );

    /**
     * 取消订单
     */
    @PostMapping("/api/order/cancel")
    Result<Void> cancelOrder(
        @RequestBody CancelOrderRequest request,
        @RequestHeader("Authorization") String token
    );

    /**
     * 查询订单列表
     */
    @GetMapping("/api/order/user/list")
    Result<Object> getOrdersByUserId(
        @RequestParam Long userId,
        @RequestParam(required = false) Integer status,
        @RequestParam Integer page,
        @RequestParam Integer size,
        @RequestHeader("Authorization") String token
    );
}
