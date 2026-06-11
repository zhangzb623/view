package com.learning.chat.handler;

import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.ChatPacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("chat server exception", cause);
        ChatPacket response = new ChatPacket();
        response.setType(ChatPacketType.ERROR);
        response.setSuccess(false);
        response.setReason(cause.getMessage() == null ? "internal server error" : cause.getMessage());
        response.setTimestamp(System.currentTimeMillis());
        ctx.writeAndFlush(response).addListener(future -> ctx.close());
    }
}
