package com.learning.order.controller;

import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.order.dto.CancelOrderRequest;
import com.learning.order.dto.CreateOrderRequest;
import com.learning.order.dto.OrderDTO;
import com.learning.order.dto.OrderQueryRequest;
import com.learning.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@Tag(name = "订单管理", description = "订单CRUD、支付、取消、发货等接口")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单", description = "创建新订单")
    @PostMapping("/create")
    public Result<Long> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                     @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        try {
            Long orderId = orderService.createOrder(request, userId);
            return Result.success("订单创建成功", orderId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return Result.fail("创建订单失败");
        }
    }

    @Operation(summary = "支付订单", description = "支付订单")
    @PostMapping("/{orderId}/pay")
    public Result<Void> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "交易流水号") @RequestParam String transactionId,
            @Parameter(description = "支付方式") @RequestParam Integer paymentMethod) {
        try {
            orderService.payOrder(orderId, transactionId, paymentMethod);
            return Result.success("支付成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("支付订单失败", e);
            return Result.fail("支付失败");
        }
    }

    @Operation(summary = "取消订单", description = "取消订单")
    @PostMapping("/cancel")
    public Result<Void> cancelOrder(@Valid @RequestBody CancelOrderRequest request) {
        try {
            orderService.cancelOrder(request);
            return Result.success("取消成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.fail("取消失败");
        }
    }

    @Operation(summary = "根据订单ID查询订单", description = "根据订单ID获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderDTO> getOrderByOrderId(@Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            OrderDTO order = orderService.getOrderByOrderId(orderId);
            return Result.success(order);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询订单列表", description = "根据用户ID查询订单列表（分页）")
    @GetMapping("/user/list")
    public Result<PageResult<OrderDTO>> getOrdersByUserId(OrderQueryRequest request) {
        try {
            PageResult<OrderDTO> pageResult = orderService.getOrdersByUserId(request);
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("查询订单列表失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "更新订单状态", description = "更新订单状态")
    @PutMapping("/{orderId}/status")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "订单状态") @RequestParam Integer status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return Result.success("状态更新成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return Result.fail("更新失败");
        }
    }

    @Operation(summary = "发货", description = "订单发货")
    @PostMapping("/{orderId}/ship")
    public Result<Void> shipOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "物流单号") @RequestParam String trackingNumber) {
        try {
            orderService.shipOrder(orderId, trackingNumber);
            return Result.success("发货成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("发货失败", e);
            return Result.fail("发货失败");
        }
    }

    @Operation(summary = "完成订单", description = "确认收货，完成订单")
    @PostMapping("/{orderId}/complete")
    public Result<Void> completeOrder(@Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            orderService.completeOrder(orderId);
            return Result.success("订单完成", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("完成订单失败", e);
            return Result.fail("完成失败");
        }
    }
}
