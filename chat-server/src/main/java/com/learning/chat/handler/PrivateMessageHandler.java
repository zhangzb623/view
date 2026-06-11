package com.learning.chat.handler;

import com.learning.chat.log.ChatLogService;
import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.ChatPacketType;
import com.learning.chat.model.DeliveryResult;
import com.learning.chat.service.ChatRoutingService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class PrivateMessageHandler extends SimpleChannelInboundHandler<ChatPacket> {

    private final ChatRoutingService chatRoutingService;
    private final ChatLogService chatLogService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) {
        if (msg.getType() != ChatPacketType.PRIVATE_MESSAGE) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg.getFromUserId() == null || msg.getToUserId() == null || !StringUtils.hasText(msg.getContent())) {
            ctx.writeAndFlush(errorResponse(msg, "fromUserId, toUserId or content missing"));
            return;
        }
        DeliveryResult result = chatRoutingService.sendPrivateMessage(msg);
        chatLogService.logMessage(msg, result.isSuccess());
        ctx.writeAndFlush(buildResponse(msg, result));
    }

    private ChatPacket buildResponse(ChatPacket request, DeliveryResult result) {
        ChatPacket response = new ChatPacket();
        response.setType(result.isSuccess() ? ChatPacketType.PRIVATE_MESSAGE : ChatPacketType.ERROR);
        response.setRequestId(request.getRequestId());
        response.setMessageId(request.getMessageId());
        response.setFromUserId(request.getFromUserId());
        response.setToUserId(request.getToUserId());
        response.setContent(request.getContent());
        response.setSuccess(result.isSuccess());
        response.setReason(result.getReason());
        response.setDeliveredTo(result.getDeliveredTo());
        response.setDeliveredCount(result.getDeliveredCount());
        response.setTimestamp(System.currentTimeMillis());
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
        response.setDeliveredCount(0L);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
