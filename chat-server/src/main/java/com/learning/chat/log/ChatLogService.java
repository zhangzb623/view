package com.learning.chat.log;

import com.learning.chat.model.ChatPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatLogService {

    public void logLogin(Long userId) {
        log.info("chat login: userId={}", userId);
    }

    public void logLogout(Long userId) {
        log.info("chat logout: userId={}", userId);
    }

    public void logMessage(ChatPacket packet, boolean success) {
        log.info("chat message: type={}, requestId={}, messageId={}, fromUserId={}, toUserId={}, groupId={}, success={}",
                packet.getType(), packet.getRequestId(), packet.getMessageId(), packet.getFromUserId(), packet.getToUserId(), packet.getGroupId(), success);
    }

    public void logReadAck(ChatPacket packet, boolean success) {
        log.info("chat read-ack: requestId={}, messageId={}, fromUserId={}, toUserId={}, success={}",
                packet.getRequestId(), packet.getMessageId(), packet.getFromUserId(), packet.getToUserId(), success);
    }

    public void logHeartbeat(Long userId) {
        log.debug("chat heartbeat: userId={}", userId);
    }
}
