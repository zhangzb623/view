package com.learning.chat.service;

import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.DeliveryResult;

public interface ChatRoutingService {
    DeliveryResult sendPrivateMessage(ChatPacket packet);
    DeliveryResult sendGroupMessage(ChatPacket packet);
}
