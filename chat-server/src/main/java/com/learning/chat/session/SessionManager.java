package com.learning.chat.session;

import com.learning.chat.model.ChatSession;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private static final AttributeKey<ChatSession> SESSION_KEY = AttributeKey.valueOf("chat.session");

    private final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, Long> channelUserMap = new ConcurrentHashMap<>();
    private final Map<Long, ChannelGroup> groupChannelMap = new ConcurrentHashMap<>();

    public void bind(Long userId, String nickname, Channel channel) {
        Channel previousChannel = userChannelMap.put(userId, channel);
        if (previousChannel != null && previousChannel != channel) {
            channelUserMap.remove(previousChannel);
            previousChannel.attr(SESSION_KEY).set(null);
            removeFromGroups(previousChannel);
        }

        Long previousUserId = channelUserMap.put(channel, userId);
        if (previousUserId != null && !previousUserId.equals(userId)) {
            userChannelMap.remove(previousUserId, channel);
        }

        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setNickname(nickname);
        session.setChannel(channel);
        session.setLastActiveTime(System.currentTimeMillis());
        channel.attr(SESSION_KEY).set(session);
    }

    public void refresh(Channel channel) {
        ChatSession session = getSession(channel);
        if (session != null) {
            session.setLastActiveTime(System.currentTimeMillis());
        }
    }

    public void unbind(Channel channel) {
        Long userId = channelUserMap.remove(channel);
        if (userId != null) {
            userChannelMap.remove(userId, channel);
        }
        removeFromGroups(channel);
        channel.attr(SESSION_KEY).set(null);
    }

    public Channel getChannel(Long userId) {
        return userChannelMap.get(userId);
    }

    public boolean isOnline(Long userId) {
        Channel channel = userChannelMap.get(userId);
        return channel != null && channel.isActive();
    }

    public ChatSession getSession(Channel channel) {
        return channel.attr(SESSION_KEY).get();
    }

    public Long getUserId(Channel channel) {
        return channelUserMap.get(channel);
    }

    public ChannelGroup getOrCreateGroup(Long groupId) {
        return groupChannelMap.computeIfAbsent(groupId, id -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    public void joinGroup(Long groupId, Channel channel) {
        if (channel != null && channel.isActive()) {
            getOrCreateGroup(groupId).add(channel);
        }
    }

    public void joinGroup(Long groupId, Collection<Channel> channels) {
        ChannelGroup group = getOrCreateGroup(groupId);
        for (Channel channel : channels) {
            if (channel != null && channel.isActive()) {
                group.add(channel);
            }
        }
    }

    private void removeFromGroups(Channel channel) {
        for (ChannelGroup group : groupChannelMap.values()) {
            group.remove(channel);
        }
    }
}
