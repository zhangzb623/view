package com.learning.chat.handler;

import com.learning.chat.log.ChatLogService;
import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.ChatPacketType;
import com.learning.chat.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HeartbeatHandler extends SimpleChannelInboundHandler<ChatPacket> {

    private final SessionManager sessionManager;
    private final ChatLogService chatLogService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) {
        if (msg.getType() != ChatPacketType.HEARTBEAT) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg.getFromUserId() == null) {
            ctx.writeAndFlush(errorResponse(msg, "fromUserId missing"));
            return;
        }
        sessionManager.bind(msg.getFromUserId(), msg.getNickname(), ctx.channel());
        sessionManager.refresh(ctx.channel());
        chatLogService.logHeartbeat(msg.getFromUserId());
        ctx.writeAndFlush(successResponse(msg));
    }

    private ChatPacket successResponse(ChatPacket request) {
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.HEARTBEAT);
        response.setRequestId(request.getRequestId());
        response.setMessageId(request.getMessageId());
        response.setFromUserId(request.getFromUserId());
        response.setSuccess(true);
        response.setReason("heartbeat ok");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private ChatPacket errorResponse(ChatPacket request, String reason) {
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.ERROR);
        response.setRequestId(request.getRequestId());
        response.setMessageId(request.getMessageId());
        response.setFromUserId(request.getFromUserId());
        response.setSuccess(false);
        response.setReason(reason);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
