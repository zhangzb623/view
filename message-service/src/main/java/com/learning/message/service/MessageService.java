package com.learning.message.service;

import com.learning.message.dto.CreateMessageRequest;
import com.learning.message.dto.MessageDTO;
import com.learning.message.dto.MessageQueryRequest;
import com.learning.message.dto.MarkAsReadRequest;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 创建消息
     */
    Long createMessage(CreateMessageRequest request);

    /**
     * 根据ID查询消息
     */
    MessageDTO getMessageById(Long messageId);

    /**
     * 根据用户ID查询消息列表
     */
    List<MessageDTO> getMessagesByUserId(Long userId);

    /**
     * 根据用户ID分页查询消息
     */
    java.util.Map<String, Object> getMessagesByUserIdWithPage(MessageQueryRequest request);

    /**
     * 标记消息为已读
     */
    void markAsRead(MarkAsReadRequest request);

    /**
     * 标记所有消息为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId);

    /**
     * 统计未读消息数量
     */
    int countUnread(Long userId);
}
