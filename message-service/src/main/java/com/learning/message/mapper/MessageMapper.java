package com.learning.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.message.dto.CreateMessageRequest;
import com.learning.message.dto.MessageQueryRequest;
import com.learning.message.entity.MessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageDO> {

    /**
     * 根据用户ID分页查询消息
     */
    List<MessageDO> selectByUserIdWithPage(@Param("userId") Long userId,
                                           @Param("page") int page,
                                           @Param("size") int size);

    /**
     * 根据用户ID统计未读消息数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 批量标记已读
     */
    int batchMarkAsRead(@Param("messageIds") List<Long> messageIds);

    /**
     * 检查消息是否存在
     */
    int existsById(@Param("messageId") Long messageId);
}
