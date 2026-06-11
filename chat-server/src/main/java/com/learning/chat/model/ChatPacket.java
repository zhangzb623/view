package com.learning.chat.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatPacket {
    private ChatPacketType type;
    private String requestId;
    private String messageId;
    private Long fromUserId;
    private Long toUserId;
    private Long groupId;
    private List<Long> groupMemberIds;
    private String nickname;
    private String content;
    private Boolean success;
    private String reason;
    private String deliveredTo;
    private Long deliveredCount;
    private Long timestamp;
    private Long ackTimestamp;
}
