package com.learning.chat.handler;

import com.learning.chat.log.ChatLogService;
import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.ChatPacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReadAckHandler extends SimpleChannelInboundHandler<ChatPacket> {

    private final ChatLogService chatLogService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) {
        if (msg.getType() != ChatPacketType.READ_ACK) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg.getFromUserId() == null || msg.getToUserId() == null || msg.getMessageId() == null) {
            chatLogService.logReadAck(msg, false);
            ctx.writeAndFlush(errorResponse(msg, "fromUserId, toUserId or messageId missing"));
            return;
        }

        chatLogService.logReadAck(msg, true);
        ctx.writeAndFlush(successResponse(msg));
    }

    private ChatPacket successResponse(ChatPacket request) {
        long now = System.currentTimeMillis();
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.READ_ACK);
        response.setRequestId(request.getRequestId());
        response.setMessageId(request.getMessageId());
        response.setFromUserId(request.getFromUserId());
        response.setToUserId(request.getToUserId());
        response.setSuccess(true);
        response.setReason("read ack received");
        response.setTimestamp(now);
        response.setAckTimestamp(now);
        return response;
    }

    private ChatPacket errorResponse(ChatPacket request, String reason) {
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.ERROR);
        response.setRequestId(request.getRequestId());
        response.setMessageId(request.getMessageId());
        response.setFromUserId(request.getFromUserId());
        response.setToUserId(request.getToUserId());
        response.setSuccess(false);
        response.setReason(reason);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
