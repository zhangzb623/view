package com.learning.chat.codec;

import com.alibaba.fastjson2.JSON;
import com.learning.chat.model.ChatPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class ChatPacketEncoder extends MessageToByteEncoder<ChatPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ChatPacket msg, ByteBuf out) {
        byte[] bytes = JSON.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
