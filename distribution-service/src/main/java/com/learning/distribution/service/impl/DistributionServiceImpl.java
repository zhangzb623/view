package com.learning.distribution.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.distribution.dto.CreateDistributionRequest;
import com.learning.distribution.dto.DistributionDTO;
import com.learning.distribution.dto.DistributionQueryRequest;
import com.learning.distribution.entity.DistributionRecordDO;
import com.learning.distribution.feign.OrderFeignClient;
import com.learning.distribution.feign.UserFeignClient;
import com.learning.distribution.feign.dto.OrderSnapshotDTO;
import com.learning.distribution.mapper.DistributionRecordMapper;
import com.learning.distribution.service.DistributionService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DistributionServiceImpl implements DistributionService {

    private final DistributionRecordMapper distributionRecordMapper;
    private final OrderFeignClient orderFeignClient;
    private final UserFeignClient userFeignClient;

    @Override
    @Transactional
    public Long calculate(CreateDistributionRequest request) {
        if (request.getCommissionRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("佣金比例必须大于0");
        }

        DistributionRecordDO existing = findByOrderId(request.getOrderId());
        if (existing != null) {
            throw new BusinessException("该订单已存在分销记录");
        }

        Result<OrderSnapshotDTO> orderResult = orderFeignClient.getOrderById(request.getOrderId());
        if (orderResult == null || orderResult.getData() == null) {
            throw new BusinessException("订单不存在");
        }

        OrderSnapshotDTO order = orderResult.getData();
        if (order.getStatus() == null || order.getStatus() != 3) {
            throw new BusinessException("仅已完成订单可生成分销记录");
        }

        DistributionRecordDO record = new DistributionRecordDO();
        record.setOrderId(order.getOrderId());
        record.setOrderUserId(order.getUserId());
        record.setDistributorUserId(request.getDistributorUserId());
        record.setProductId(order.getProductId());
        record.setOrderAmount(order.getTotalPrice());
        record.setCommissionRate(request.getCommissionRate());
        record.setCommissionAmount(calculateCommission(order.getTotalPrice(), request.getCommissionRate()));
        record.setStatus(0);
        record.setRemark(request.getRemark());
        record.setDeleted(0);

        distributionRecordMapper.insert(record);
        return record.getDistributionId();
    }

    @Override
    public DistributionDTO getById(Long distributionId) {
        DistributionRecordDO record = distributionRecordMapper.selectById(distributionId);
        if (record == null || Integer.valueOf(1).equals(record.getDeleted())) {
            throw new BusinessException("分销记录不存在");
        }
        return toDTO(record);
    }

    @Override
    public DistributionDTO getByOrderId(Long orderId) {
        DistributionRecordDO record = findByOrderId(orderId);
        if (record == null) {
            throw new BusinessException("分销记录不存在");
        }
        return toDTO(record);
    }

    @Override
    public PageResult<DistributionDTO> getByDistributor(DistributionQueryRequest request) {
        LambdaQueryWrapper<DistributionRecordDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DistributionRecordDO::getDeleted, 0)
                .eq(DistributionRecordDO::getDistributorUserId, request.getDistributorUserId())
                .orderByDesc(DistributionRecordDO::getCreateTime);
        if (request.getStatus() != null) {
            wrapper.eq(DistributionRecordDO::getStatus, request.getStatus());
        }

        Page<DistributionRecordDO> page = new Page<>(request.getCurrent(), request.getSize());
        Page<DistributionRecordDO> pageData = distributionRecordMapper.selectPage(page, wrapper);
        List<DistributionDTO> records = pageData.getRecords().stream().map(this::toDTO).toList();
        return PageResult.of(records, pageData.getTotal(), pageData.getCurrent(), pageData.getSize());
    }

    @Override
    @Transactional
    @GlobalTransactional(name = "distribution-settle", rollbackFor = Exception.class)
    public void settle(Long distributionId, boolean simulateRollback) {
        DistributionRecordDO record = distributionRecordMapper.selectById(distributionId);
        if (record == null || Integer.valueOf(1).equals(record.getDeleted())) {
            throw new BusinessException("分销记录不存在");
        }
        if (!Integer.valueOf(0).equals(record.getStatus())) {
            throw new BusinessException("只有待结算记录可结算");
        }

        Result<Void> userResult = userFeignClient.addBalance(record.getDistributorUserId(), record.getCommissionAmount());
        if (userResult == null || userResult.getCode() == null || userResult.getCode() != 200) {
            throw new BusinessException("分销结算失败：用户余额增加失败");
        }

        if (simulateRollback) {
            throw new RuntimeException("mock seata rollback");
        }

        record.setStatus(1);
        record.setSettledTime(LocalDateTime.now());
        distributionRecordMapper.updateById(record);
    }

    private DistributionRecordDO findByOrderId(Long orderId) {
        return distributionRecordMapper.selectOne(new LambdaQueryWrapper<DistributionRecordDO>()
                .eq(DistributionRecordDO::getOrderId, orderId)
                .eq(DistributionRecordDO::getDeleted, 0));
    }

    private BigDecimal calculateCommission(BigDecimal orderAmount, BigDecimal commissionRate) {
        return orderAmount.multiply(commissionRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private DistributionDTO toDTO(DistributionRecordDO record) {
        DistributionDTO dto = new DistributionDTO();
        BeanUtils.copyProperties(record, dto);
        dto.setStatusText(getStatusText(record.getStatus()));
        return dto;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "已计算";
            case 1 -> "已结算";
            case 2 -> "已取消";
            default -> "未知";
        };
    }
}
