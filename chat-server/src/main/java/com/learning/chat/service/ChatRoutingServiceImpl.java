package com.learning.chat.service;

import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.DeliveryResult;
import com.learning.chat.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoutingServiceImpl implements ChatRoutingService {

    private final SessionManager sessionManager;

    @Override
    public DeliveryResult sendPrivateMessage(ChatPacket packet) {
        DeliveryResult result = new DeliveryResult();
        if (packet.getToUserId() == null) {
            result.setSuccess(false);
            result.setReason("target user missing");
            result.setDeliveredCount(0L);
            return result;
        }
        if (packet.getFromUserId() != null && packet.getFromUserId().equals(packet.getToUserId())) {
            result.setSuccess(false);
            result.setReason("cannot send private message to self");
            result.setDeliveredTo(String.valueOf(packet.getToUserId()));
            result.setDeliveredCount(0L);
            return result;
        }

        Channel target = sessionManager.getChannel(packet.getToUserId());
        if (target == null || !target.isActive()) {
            result.setSuccess(false);
            result.setReason("target user offline");
            result.setDeliveredTo(String.valueOf(packet.getToUserId()));
            result.setDeliveredCount(0L);
            return result;
        }

        target.writeAndFlush(packet);
        result.setSuccess(true);
        result.setReason("delivered");
        result.setDeliveredTo(String.valueOf(packet.getToUserId()));
        result.setDeliveredCount(1L);
        return result;
    }

    @Override
    public DeliveryResult sendGroupMessage(ChatPacket packet) {
        DeliveryResult result = new DeliveryResult();
        if (packet.getGroupId() == null) {
            result.setSuccess(false);
            result.setReason("group id missing");
            result.setDeliveredCount(0L);
            return result;
        }

        List<Channel> members = new ArrayList<>();
        if (packet.getGroupMemberIds() != null) {
            for (Long memberId : packet.getGroupMemberIds()) {
                if (memberId == null) {
                    continue;
                }
                Channel memberChannel = sessionManager.getChannel(memberId);
                if (memberChannel != null && memberChannel.isActive()) {
                    members.add(memberChannel);
                }
            }
        }

        if (!members.isEmpty()) {
            sessionManager.joinGroup(packet.getGroupId(), members);
        }

        ChannelGroup group = sessionManager.getOrCreateGroup(packet.getGroupId());
        Channel senderChannel = sessionManager.getChannel(packet.getFromUserId());
        if (senderChannel != null && senderChannel.isActive()) {
            group.add(senderChannel);
        }

        if (group.isEmpty()) {
            result.setSuccess(false);
            result.setReason("group empty");
            result.setDeliveredTo(String.valueOf(packet.getGroupId()));
            result.setDeliveredCount(0L);
            return result;
        }

        long deliveredCount = group.stream()
                .filter(Channel::isActive)
                .filter(channel -> senderChannel == null || channel != senderChannel)
                .count();
        if (deliveredCount == 0) {
            result.setSuccess(false);
            result.setReason("group has no other active members");
            result.setDeliveredTo(String.valueOf(packet.getGroupId()));
            result.setDeliveredCount(0L);
            return result;
        }

        group.writeAndFlush(packet, channel -> channel.isActive() && (senderChannel == null || channel != senderChannel));
        result.setSuccess(true);
        result.setReason("broadcasted");
        result.setDeliveredTo(String.valueOf(packet.getGroupId()));
        result.setDeliveredCount(deliveredCount);
        return result;
    }
}
