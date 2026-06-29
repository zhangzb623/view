package com.learning.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.common.starter.exception.BusinessException;
import com.learning.common.starter.utils.CacheHelper;
import com.learning.message.dto.*;
import com.learning.message.entity.MessageDO;
import com.learning.message.mapper.MessageMapper;
import com.learning.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private CacheHelper cacheHelper;

    @Override
    @Transactional
    public Long createMessage(CreateMessageRequest request) {
        MessageDO message = new MessageDO();
        BeanUtils.copyProperties(request, message);
        message.setStatus(0); // 未读
        message.setNotified(0); // 未发送通知

        messageMapper.insert(message);

        // 更新缓存
        updateMessageCache(message);

        log.info("消息创建成功: messageId={}, userId={}, title={}",
            message.getMessageId(), message.getUserId(), message.getTitle());

        return message.getMessageId();
    }

    @Override
    public MessageDTO getMessageById(Long messageId) {
        MessageDO message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在");
        }
        return convertToMessageDTO(message);
    }

    @Override
    public List<MessageDTO> getMessagesByUserId(Long userId) {
        List<MessageDO> messages = messageMapper.selectByUserIdWithPage(userId, 0, Integer.MAX_VALUE);
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.Map<String, Object> getMessagesByUserIdWithPage(MessageQueryRequest request) {
        // 分页计算
        int offset = (request.getPage() - 1) * request.getSize();
        List<MessageDO> messages = messageMapper.selectByUserIdWithPage(request.getUserId(), offset, request.getSize());

        // 统计未读数量
        int unreadCount = messageMapper.countUnreadByUserId(request.getUserId());

        // 转换为DTO
        List<MessageDTO> messageDTOs = messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());

        // 获取总数量
        int total = messageMapper.selectCount(new LambdaQueryWrapper<MessageDO>()
                .eq(MessageDO::getUserId, request.getUserId())
                .eq(MessageDO::getDeleted, 0)).intValue();

        Map<String, Object> result = new HashMap<>();
        result.put("records", messageDTOs);
        result.put("total", total);
        result.put("current", request.getPage());
        result.put("size", request.getSize());
        result.put("unreadCount", unreadCount);

        return result;
    }

    @Override
    @Transactional
    public void markAsRead(MarkAsReadRequest request) {
        messageMapper.batchMarkAsRead(List.of(request.getMessageId()));

        // 清除缓存
        cacheHelper.delete("message:" + request.getMessageId());
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        // 查询该用户所有未读消息
        List<MessageDO> messages = messageMapper.selectByUserIdWithPage(userId, 0, Integer.MAX_VALUE);

        if (!messages.isEmpty()) {
            List<Long> messageIds = messages.stream()
                    .map(MessageDO::getMessageId)
                    .collect(Collectors.toList());

            messageMapper.batchMarkAsRead(messageIds);

            // 清除缓存
            for (Long messageId : messageIds) {
                cacheHelper.delete("message:" + messageId);
            }
        }
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        // 软删除
        messageMapper.deleteById(messageId);

        // 清除缓存
        cacheHelper.delete("message:" + messageId);
    }

    @Override
    public int countUnread(Long userId) {
        return messageMapper.countUnreadByUserId(userId);
    }

    /**
     * 更新消息缓存
     */
    private void updateMessageCache(MessageDO message) {
        MessageDTO dto = convertToMessageDTO(message);
        cacheHelper.set("message:" + message.getMessageId(), dto, 3600L, TimeUnit.SECONDS);
    }

    /**
     * 转换为MessageDTO
     */
    private MessageDTO convertToMessageDTO(MessageDO message) {
        MessageDTO dto = new MessageDTO();
        BeanUtils.copyProperties(message, dto);

        dto.setMessageTypeText(getMessageTypeText(message.getMessageType()));
        dto.setStatusText(getStatusText(message.getStatus()));
        dto.setBusinessTypeText(getBusinessTypeText(message.getBusinessType()));
        dto.setSourceText(getSourceText(message.getSource()));

        return dto;
    }

    private String getMessageTypeText(Integer messageType) {
        switch (messageType) {
            case 1: return "订单通知";
            case 2: return "支付通知";
            case 3: return "退款通知";
            case 4: return "系统通知";
            default: return "未知";
        }
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "未读";
            case 1: return "已读";
            case 2: return "已删除";
            default: return "未知";
        }
    }

    private String getBusinessTypeText(String businessType) {
        switch (businessType) {
            case "order": return "订单";
            case "payment": return "支付";
            case "refund": return "退款";
            case "system": return "系统";
            default: return "其他";
        }
    }

    private String getSourceText(Integer source) {
        switch (source) {
            case 1: return "订单服务";
            case 2: return "支付服务";
            case 3: return "系统";
            default: return "未知";
        }
    }
}
