package com.learning.chat.handler;

import com.learning.chat.log.ChatLogService;
import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.ChatPacketType;
import com.learning.chat.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginHandler extends SimpleChannelInboundHandler<ChatPacket> {

    private final SessionManager sessionManager;
    private final ChatLogService chatLogService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) {
        if (msg.getType() != ChatPacketType.LOGIN) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg.getFromUserId() == null) {
            ctx.writeAndFlush(errorResponse(msg, "fromUserId missing"));
            return;
        }
        sessionManager.bind(msg.getFromUserId(), msg.getNickname(), ctx.channel());
        chatLogService.logLogin(msg.getFromUserId());
        ctx.writeAndFlush(successResponse(msg, "login success"));
    }

    private ChatPacket successResponse(ChatPacket request, String reason) {
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.LOGIN);
        response.setRequestId(request.getRequestId());
        response.setMessageId(request.getMessageId());
        response.setFromUserId(request.getFromUserId());
        response.setNickname(request.getNickname());
        response.setSuccess(true);
        response.setReason(reason);
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
