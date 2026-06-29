package com.learning.payment.controller;

import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.payment.dto.CreatePaymentRequest;
import com.learning.payment.dto.PaymentDTO;
import com.learning.payment.dto.RefundDTO;
import com.learning.payment.dto.RefundRequest;
import com.learning.payment.service.PaymentService;
import com.learning.payment.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器 - Seata分布式事务
 */
@Slf4j
@Tag(name = "支付管理", description = "支付和退款接口 - 演示Seata分布式事务")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    @Operation(summary = "创建支付", description = "创建支付记录")
    @PostMapping("/create")
    public Result<Long> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        try {
            Long paymentId = paymentService.createPayment(request);
            return Result.success("支付记录创建成功", paymentId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建支付失败", e);
            return Result.fail("创建支付失败");
        }
    }

    @Operation(summary = "调用第三方支付", description = "模拟调用第三方支付接口")
    @PostMapping("/{paymentId}/pay")
    public Result<Void> callThirdPartyPayment(
            @Parameter(description = "支付ID") @PathVariable Long paymentId) {
        try {
            paymentService.callThirdPartyPayment(paymentId);
            return Result.success("第三方支付调用成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("第三方支付调用失败", e);
            return Result.fail("第三方支付调用失败");
        }
    }

    @Operation(summary = "查询支付详情", description = "根据支付ID查询支付详情")
    @GetMapping("/{paymentId}")
    public Result<PaymentDTO> getPaymentById(
            @Parameter(description = "支付ID") @PathVariable Long paymentId) {
        try {
            PaymentDTO payment = paymentService.getPaymentById(paymentId);
            return Result.success(payment);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询支付详情失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "根据订单ID查询支付", description = "根据订单ID查询支付详情")
    @GetMapping("/order/{orderId}")
    public Result<PaymentDTO> getPaymentByOrderId(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
            return Result.success(payment);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询支付详情失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询用户支付记录", description = "分页查询用户的支付记录")
    @GetMapping("/user/payments")
    public Result<Object> getUserPayments(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            return paymentService.getUserPayments(userId, page, size);
        } catch (Exception e) {
            log.error("查询支付记录失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询未支付支付记录", description = "查询用户的未支付支付记录")
    @GetMapping("/user/unpaid")
    public Result<PaymentDTO> getUnpaidPayment(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            PaymentDTO payment = paymentService.getUnpaidPayment(userId);
            return Result.success(payment);
        } catch (Exception e) {
            log.error("查询未支付支付记录失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "创建退款", description = "创建退款请求")
    @PostMapping("/refund/create")
    public Result<Long> createRefund(@Valid @RequestBody RefundRequest request) {
        try {
            Long refundId = refundService.createRefund(request);
            return Result.success("退款记录创建成功", refundId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建退款失败", e);
            return Result.fail("创建退款失败");
        }
    }

    @Operation(summary = "调用第三方退款", description = "模拟调用第三方退款接口")
    @PostMapping("/refund/{refundId}/refund")
    public Result<Void> callThirdPartyRefund(
            @Parameter(description = "退款ID") @PathVariable Long refundId) {
        try {
            refundService.callThirdPartyRefund(refundId);
            return Result.success("第三方退款调用成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("第三方退款调用失败", e);
            return Result.fail("第三方退款调用失败");
        }
    }

    @Operation(summary = "查询退款详情", description = "根据退款ID查询退款详情")
    @GetMapping("/refund/{refundId}")
    public Result<RefundDTO> getRefundById(
            @Parameter(description = "退款ID") @PathVariable Long refundId) {
        try {
            RefundDTO refund = refundService.getRefundById(refundId);
            return Result.success(refund);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询退款详情失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "根据订单ID查询退款", description = "根据订单ID查询退款详情")
    @GetMapping("/refund/order/{orderId}")
    public Result<RefundDTO> getRefundByOrderId(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            RefundDTO refund = refundService.getRefundByOrderId(orderId);
            return Result.success(refund);
        } catch (Exception e) {
            log.error("查询退款详情失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询订单的退款记录", description = "查询订单的所有退款记录")
    @GetMapping("/refund/order/{orderId}/all")
    public Result<java.util.List<RefundDTO>> getOrderRefunds(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            java.util.List<RefundDTO> refunds = refundService.getOrderRefunds(orderId);
            return Result.success(refunds);
        } catch (Exception e) {
            log.error("查询退款记录失败", e);
            return Result.fail("查询失败");
        }
    }
}
