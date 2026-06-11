package com.learning.chat.handler;

import com.learning.chat.log.ChatLogService;
import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.ChatPacketType;
import com.learning.chat.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutHandler extends SimpleChannelInboundHandler<ChatPacket> {

    private final SessionManager sessionManager;
    private final ChatLogService chatLogService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) {
        if (msg.getType() != ChatPacketType.LOGOUT) {
            ctx.fireChannelRead(msg);
            return;
        }
        Long userId = msg.getFromUserId() != null ? msg.getFromUserId() : sessionManager.getUserId(ctx.channel());
        if (userId != null) {
            chatLogService.logLogout(userId);
        }
        sessionManager.unbind(ctx.channel());
        ctx.writeAndFlush(successResponse(msg, userId)).addListener(future -> ctx.close());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Long userId = sessionManager.getUserId(ctx.channel());
        if (userId != null) {
            chatLogService.logLogout(userId);
        }
        sessionManager.unbind(ctx.channel());
        ctx.fireChannelInactive();
    }

    private ChatPacket successResponse(ChatPacket request, Long userId) {
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.LOGOUT);
        response.setRequestId(request.getRequestId());
        response.setFromUserId(userId);
        response.setSuccess(true);
        response.setReason("logout success");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
