package com.learning.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.payment.dto.CreatePaymentRequest;
import com.learning.payment.dto.RefundRequest;
import com.learning.payment.entity.PaymentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付Mapper接口
 */
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentDO> {

    /**
     * 创建支付记录
     */
    int createPayment(PaymentDO payment);

    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("paymentId") Long paymentId, @Param("status") Integer status);

    /**
     * 更新支付渠道信息
     */
    int updatePaymentChannelInfo(@Param("paymentId") Long paymentId,
                                 @Param("transactionId") String transactionId,
                                 @Param("channelCode") String channelCode,
                                 @Param("channelMessage") String channelMessage);

    /**
     * 检查支付记录是否存在
     */
    @Select("SELECT COUNT(*) FROM t_payment WHERE payment_id = #{paymentId} AND deleted = 0")
    int existsById(@Param("paymentId") Long paymentId);

    /**
     * 根据订单ID查询支付记录
     */
    @Select("SELECT * FROM t_payment WHERE order_id = #{orderId} AND deleted = 0")
    PaymentDO selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据用户ID查询支付记录
     */
    @Select("SELECT * FROM t_payment WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    PaymentDO selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询未支付支付记录
     */
    @Select("SELECT * FROM t_payment WHERE user_id = #{userId} AND status = 0 AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    PaymentDO selectUnpaidByUserId(@Param("userId") Long userId);

    /**
     * 查询用户支付记录列表
     */
    List<PaymentDO> selectByUserIdWithPage(@Param("userId") Long userId, @Param("page") int page, @Param("size") int size);
}
