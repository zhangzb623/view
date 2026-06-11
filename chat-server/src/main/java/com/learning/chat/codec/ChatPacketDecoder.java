package com.learning.chat.codec;

import com.alibaba.fastjson2.JSON;
import com.learning.chat.model.ChatPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatPacketDecoder extends ByteToMessageDecoder {

    private static final int HEADER_SIZE = 4;
    private static final int MAX_PACKET_SIZE = 64 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }

        in.markReaderIndex();
        int packetLength = in.readInt();
        if (packetLength <= 0 || packetLength > MAX_PACKET_SIZE) {
            throw new IllegalArgumentException("invalid packet length: " + packetLength);
        }
        if (in.readableBytes() < packetLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[packetLength];
        in.readBytes(bytes);
        ChatPacket packet = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), ChatPacket.class);
        if (packet == null || packet.getType() == null) {
            throw new IllegalArgumentException("invalid chat packet");
        }
        out.add(packet);
    }
}
