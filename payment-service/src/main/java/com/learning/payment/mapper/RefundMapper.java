package com.learning.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.payment.dto.RefundRequest;
import com.learning.payment.entity.RefundDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 退款Mapper接口
 */
@Mapper
public interface RefundMapper extends BaseMapper<RefundDO> {

    /**
     * 创建退款记录
     */
    int createRefund(RefundDO refund);

    /**
     * 更新退款状态
     */
    int updateRefundStatus(@Param("refundId") Long refundId, @Param("status") Integer status);

    /**
     * 检查退款记录是否存在
     */
    int existsById(@Param("refundId") Long refundId);

    /**
     * 根据订单ID查询退款记录
     */
    RefundDO selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询订单的退款记录
     */
    List<RefundDO> selectRefundsByOrderId(@Param("orderId") Long orderId);
}
