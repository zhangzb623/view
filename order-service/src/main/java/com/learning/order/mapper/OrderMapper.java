package com.learning.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.order.dto.OrderQueryRequest;
import com.learning.order.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单Mapper接口 - 演示ShardingJDBC分片
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {

    /**
     * 根据条件分页查询订单
     */
    IPage<OrderDO> selectPageByCondition(Page<OrderDO> page, @Param("request") OrderQueryRequest request);

    /**
     * 根据用户ID查询订单总数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 检查订单是否存在
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE order_id = #{orderId} AND deleted = 0")
    int existsById(@Param("orderId") Long orderId);

    /**
     * 根据用户ID查询未支付订单
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND status = 0 AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    OrderDO selectUnpaidOrderByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询订单列表（不分页）
     */
    List<OrderDO> selectByUserId(@Param("userId") Long userId);
}
