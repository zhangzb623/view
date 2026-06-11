package com.learning.chat.bootstrap;

import com.learning.chat.codec.ChatPacketDecoder;
import com.learning.chat.codec.ChatPacketEncoder;
import com.learning.chat.handler.ExceptionHandler;
import com.learning.chat.handler.GroupMessageHandler;
import com.learning.chat.handler.HeartbeatHandler;
import com.learning.chat.handler.LoginHandler;
import com.learning.chat.handler.LogoutHandler;
import com.learning.chat.handler.PrivateMessageHandler;
import com.learning.chat.handler.ReadAckHandler;
import com.learning.chat.log.ChatLogService;
import com.learning.chat.service.ChatRoutingService;
import com.learning.chat.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatServerBootstrap implements InitializingBean, DisposableBean {

    @Value("${chat.server.port:9000}")
    private int port;

    private final SessionManager sessionManager;
    private final ChatLogService chatLogService;
    private final ChatRoutingService chatRoutingService;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public ChatServerBootstrap(SessionManager sessionManager,
                               ChatLogService chatLogService,
                               ChatRoutingService chatRoutingService) {
        this.sessionManager = sessionManager;
        this.chatLogService = chatLogService;
        this.chatRoutingService = chatRoutingService;
    }

    @Override
    public void afterPropertiesSet() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        new Thread(this::startServer, "chat-server-bootstrap").start();
    }

    private void startServer() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture future = bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ChatPacketDecoder());
                            ch.pipeline().addLast(new ChatPacketEncoder());
                            ch.pipeline().addLast(new LoginHandler(sessionManager, chatLogService));
                            ch.pipeline().addLast(new HeartbeatHandler(sessionManager, chatLogService));
                            ch.pipeline().addLast(new PrivateMessageHandler(chatRoutingService, chatLogService));
                            ch.pipeline().addLast(new GroupMessageHandler(chatRoutingService, chatLogService));
                            ch.pipeline().addLast(new ReadAckHandler(chatLogService));
                            ch.pipeline().addLast(new LogoutHandler(sessionManager, chatLogService));
                            ch.pipeline().addLast(new ExceptionHandler());
                        }
                    })
                    .bind(port)
                    .sync();
            log.info("Chat Server started on port {}", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Chat Server stopped unexpectedly", e);
        }
    }

    @Override
    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
