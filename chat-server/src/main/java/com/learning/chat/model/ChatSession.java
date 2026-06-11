package com.learning.chat.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ChatSession {
    private Long userId;
    private String nickname;
    private Channel channel;
    private Long lastActiveTime;
}
