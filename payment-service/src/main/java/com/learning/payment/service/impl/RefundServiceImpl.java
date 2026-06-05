package com.learning.payment.service.impl;

import com.learning.common.starter.exception.BusinessException;
import com.learning.payment.dto.RefundDTO;
import com.learning.payment.dto.RefundRequest;
import com.learning.payment.entity.PaymentDO;
import com.learning.payment.entity.RefundDO;
import com.learning.payment.mapper.PaymentMapper;
import com.learning.payment.mapper.RefundMapper;
import com.learning.payment.service.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 退款服务实现类
 */
@Slf4j
@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    /**
     * 创建退款（使用TCC模式或Saga模式）
     */
    @Override
    @Transactional
    public Long createRefund(RefundRequest request) {
        // 1. 检查支付记录是否存在
        PaymentDO payment = paymentMapper.selectById(request.getPaymentId());
        if (payment == null || payment.getDeleted() == 1) {
            throw new BusinessException("支付记录不存在");
        }

        if (payment.getRefundStatus() != 0) {
            throw new BusinessException("支付记录已有退款操作");
        }

        // 2. 创建退款记录
        RefundDO refund = new RefundDO();
        refund.setUserId(payment.getUserId());
        refund.setOrderId(payment.getOrderId());
        refund.setPaymentId(request.getPaymentId());
        refund.setRefundAmount(request.getRefundAmount() != null ? request.getRefundAmount() : payment.getAmount());
        refund.setRefundReason(request.getRefundReason());
        refund.setRefundChannel(request.getRefundChannel());
        refund.setStatus(0); // 待处理

        refundMapper.createRefund(refund);

        log.info("退款记录创建成功: refundId={}, orderId={}, refundAmount={}",
            refund.getRefundId(), refund.getOrderId(), refund.getRefundAmount());

        return refund.getRefundId();
    }

    /**
     * 调用第三方退款接口（模拟）
     */
    @Override
    public void callThirdPartyRefund(Long refundId) {
        RefundDO refund = refundMapper.selectById(refundId);
        if (refund == null || refund.getDeleted() == 1) {
            throw new BusinessException("退款记录不存在");
        }

        if (refund.getStatus() != 0) {
            throw new BusinessException("退款状态异常");
        }

        // 模拟调用第三方退款接口
        try {
            // 模拟网络请求延迟
            Thread.sleep(1500);

            // 模拟第三方退款成功
            String refundTransactionId = "RFN" + System.currentTimeMillis();

            // 更新退款记录
            refundMapper.updateRefundStatus(refundId, 2); // 成功
            refund.setRefundTransactionId(refundTransactionId);
            refund.setCompleteTime(java.time.LocalDateTime.now());

            // 更新支付记录的退款状态
            PaymentDO payment = paymentMapper.selectById(refund.getPaymentId());
            payment.setRefundStatus(2); // 已退款
            payment.setRefundAmount(refund.getRefundAmount());
            paymentMapper.updateById(payment);

            log.info("第三方退款成功: refundId={}, refundTransactionId={}", refundId, refundTransactionId);

            // TODO: 调用订单服务处理退款

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("退款处理异常");
        } catch (Exception e) {
            log.error("第三方退款失败: refundId={}", refundId, e);
            refundMapper.updateRefundStatus(refundId, 3); // 失败
            throw new BusinessException("退款处理失败");
        }
    }

    @Override
    public RefundDTO getRefundById(Long refundId) {
        RefundDO refund = refundMapper.selectById(refundId);
        if (refund == null || refund.getDeleted() == 1) {
            throw new BusinessException("退款记录不存在");
        }

        RefundDTO dto = convertToRefundDTO(refund);
        return dto;
    }

    @Override
    public RefundDTO getRefundByOrderId(Long orderId) {
        RefundDO refund = refundMapper.selectByOrderId(orderId);
        if (refund == null || refund.getDeleted() == 1) {
            return null;
        }
        return convertToRefundDTO(refund);
    }

    @Override
    public java.util.List<RefundDTO> getOrderRefunds(Long orderId) {
        return refundMapper.selectRefundsByOrderId(orderId).stream()
                .map(this::convertToRefundDTO)
                .toList();
    }

    /**
     * 转换为RefundDTO
     */
    private RefundDTO convertToRefundDTO(RefundDO refund) {
        RefundDTO dto = new RefundDTO();
        BeanUtils.copyProperties(refund, dto);

        // 退款状态转换
        dto.setStatusText(getStatusText(refund.getStatus()));

        return dto;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "成功";
            case 3: return "失败";
            default: return "未知";
        }
    }
}
