# Chat Server Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Netty-based chat server that supports login, heartbeat, private messaging, and group broadcasting on a single node.

**Architecture:** The first iteration uses a plain TCP Netty server with a JSON packet protocol. A small set of handlers dispatches messages by type, while a session manager tracks online users and group channels. Persistence is intentionally minimal in phase 1: basic logs only, with richer features collected in `docs/future-features/chat-server-phase-2.md`.

**Tech Stack:** Netty 4.1.x, Spring Boot, FastJSON2, Lombok, common-starter, Java 17.

---

## File Structure

### New module files
- Create: `chat-server/pom.xml` — module dependencies and packaging
- Create: `chat-server/src/main/resources/application.yml` — server port and logging config
- Create: `chat-server/src/main/java/com/learning/chat/ChatServerApplication.java` — bootstrap entrypoint
- Create: `chat-server/src/main/java/com/learning/chat/bootstrap/ChatServerBootstrap.java` — Netty server startup

### Core model files
- Create: `chat-server/src/main/java/com/learning/chat/model/ChatPacket.java`
- Create: `chat-server/src/main/java/com/learning/chat/model/ChatPacketType.java`
- Create: `chat-server/src/main/java/com/learning/chat/model/ChatSession.java`
- Create: `chat-server/src/main/java/com/learning/chat/model/DeliveryResult.java`

### Codec files
- Create: `chat-server/src/main/java/com/learning/chat/codec/ChatPacketDecoder.java`
- Create: `chat-server/src/main/java/com/learning/chat/codec/ChatPacketEncoder.java`

### Session and service files
- Create: `chat-server/src/main/java/com/learning/chat/session/SessionManager.java`
- Create: `chat-server/src/main/java/com/learning/chat/service/ChatRoutingService.java`
- Create: `chat-server/src/main/java/com/learning/chat/log/ChatLogService.java`

### Handler files
- Create: `chat-server/src/main/java/com/learning/chat/handler/LoginHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/HeartbeatHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/PrivateMessageHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/GroupMessageHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/LogoutHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/ExceptionHandler.java`

### Documentation files
- Create: `chat-server/README.md`
- Create: `docs/future-features/chat-server-phase-2.md` — already added as the phase-2 backlog reference
- Modify: `README.md` — add Chat Server progress and quick-start examples
- Modify: `docs/future-features/README.md` — keep it as the future work index

---

### Task 1: Create the chat-server module skeleton

**Files:**
- Create: `chat-server/pom.xml`
- Create: `chat-server/src/main/resources/application.yml`
- Create: `chat-server/src/main/java/com/learning/chat/ChatServerApplication.java`
- Create: `chat-server/src/main/java/com/learning/chat/bootstrap/ChatServerBootstrap.java`
- Modify: `pom.xml`

- [ ] **Step 1: Add the module to the parent POM**

Ensure the root `pom.xml` contains:

```xml
<modules>
    <module>common/common-api</module>
    <module>common/common-starter</module>
    <module>common/common-util</module>
    <module>common/common-domain</module>
    <module>gateway/gateway-service</module>
    <module>user-service</module>
    <module>product-service</module>
    <module>order-service</module>
    <module>message-service</module>
    <module>payment-service</module>
    <module>distribution-service</module>
    <module>scheduler-service</module>
    <module>admin-service</module>
    <module>chat-server</module>
</modules>
```

- [ ] **Step 2: Create `chat-server/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.learning</groupId>
        <artifactId>spring-cloud-learning-system</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>chat-server</artifactId>
    <name>Chat Server</name>
    <description>Netty聊天服务器</description>

    <dependencies>
        <dependency>
            <groupId>com.learning</groupId>
            <artifactId>common-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: Create application entrypoint and bootstrap class**

`chat-server/src/main/java/com/learning/chat/ChatServerApplication.java`

```java
package com.learning.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.learning")
public class ChatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }
}
```

`chat-server/src/main/java/com/learning/chat/bootstrap/ChatServerBootstrap.java`

```java
package com.learning.chat.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void afterPropertiesSet() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        new Thread(this::start).start();
    }

    private void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture future = bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .bind(port)
                .sync();
            log.info("Chat Server started on port {}", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Chat Server stopped", e);
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
```

- [ ] **Step 4: Create `application.yml`**

```yaml
server:
  port: 9000

chat:
  server:
    port: 9000

logging:
  level:
    com.learning.chat: DEBUG
```

- [ ] **Step 5: Run compile check**

Run: `mvn -pl chat-server -am compile`
Expected: Chat Server module resolves Netty and FastJSON2 dependencies.

- [ ] **Step 6: Commit**

```bash
git add pom.xml chat-server/pom.xml chat-server/src/main/java/com/learning/chat/ChatServerApplication.java chat-server/src/main/java/com/learning/chat/bootstrap/ChatServerBootstrap.java chat-server/src/main/resources/application.yml
git commit -m "feat: scaffold chat server module"
```

### Task 2: Add packet models and codec

**Files:**
- Create: `chat-server/src/main/java/com/learning/chat/model/ChatPacketType.java`
- Create: `chat-server/src/main/java/com/learning/chat/model/ChatPacket.java`
- Create: `chat-server/src/main/java/com/learning/chat/model/ChatSession.java`
- Create: `chat-server/src/main/java/com/learning/chat/model/DeliveryResult.java`
- Create: `chat-server/src/main/java/com/learning/chat/codec/ChatPacketDecoder.java`
- Create: `chat-server/src/main/java/com/learning/chat/codec/ChatPacketEncoder.java`

- [ ] **Step 1: Write the packet type enum**

```java
package com.learning.chat.model;

public enum ChatPacketType {
    LOGIN,
    HEARTBEAT,
    PRIVATE_MESSAGE,
    GROUP_MESSAGE,
    READ_ACK,
    LOGOUT,
    ERROR
}
```

- [ ] **Step 2: Write the packet and session models**

```java
package com.learning.chat.model;

import lombok.Data;

@Data
public class ChatPacket {
    private ChatPacketType type;
    private String requestId;
    private Long fromUserId;
    private Long toUserId;
    private Long groupId;
    private String content;
    private Long timestamp;
}
```

```java
package com.learning.chat.model;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ChatSession {
    private Long userId;
    private String nickname;
    private Channel channel;
    private Long lastActiveTime;
}
```

```java
package com.learning.chat.model;

import lombok.Data;

@Data
public class DeliveryResult {
    private boolean success;
    private String reason;
    private String deliveredTo;
}
```

- [ ] **Step 3: Write the encoder and decoder**

Use FastJSON2 to convert between bytes and `ChatPacket`.

- [ ] **Step 4: Run compile check**

Run: `mvn -pl chat-server -am compile`
Expected: model and codec classes compile.

- [ ] **Step 5: Commit**

```bash
git add chat-server/src/main/java/com/learning/chat/model/*.java chat-server/src/main/java/com/learning/chat/codec/*.java
git commit -m "feat: add chat packet model and codec"
```

### Task 3: Add session management and routing services

**Files:**
- Create: `chat-server/src/main/java/com/learning/chat/session/SessionManager.java`
- Create: `chat-server/src/main/java/com/learning/chat/service/ChatRoutingService.java`
- Create: `chat-server/src/main/java/com/learning/chat/log/ChatLogService.java`

- [ ] **Step 1: Create session manager**

```java
package com.learning.chat.session;

import com.learning.chat.model.ChatSession;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, Long> channelUserMap = new ConcurrentHashMap<>();
    private final Map<Long, ChannelGroup> groupChannelMap = new ConcurrentHashMap<>();

    public void bind(Long userId, Channel channel) { ... }
    public void unbind(Channel channel) { ... }
    public Channel getChannel(Long userId) { ... }
    public boolean isOnline(Long userId) { ... }
    public ChannelGroup getOrCreateGroup(Long groupId) { ... }
}
```

- [ ] **Step 2: Create routing service**

```java
package com.learning.chat.service;

import com.learning.chat.model.ChatPacket;
import com.learning.chat.model.DeliveryResult;

public interface ChatRoutingService {
    DeliveryResult sendPrivateMessage(ChatPacket packet);
    DeliveryResult sendGroupMessage(ChatPacket packet);
}
```

- [ ] **Step 3: Create basic log service**

```java
package com.learning.chat.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatLogService {
    public void logLogin(Long userId) { ... }
    public void logMessage(ChatPacket packet, boolean success) { ... }
    public void logHeartbeat(Long userId) { ... }
}
```

- [ ] **Step 4: Run compile check**

Run: `mvn -pl chat-server -am compile`
Expected: session and routing services compile.

- [ ] **Step 5: Commit**

```bash
git add chat-server/src/main/java/com/learning/chat/session/*.java chat-server/src/main/java/com/learning/chat/service/*.java chat-server/src/main/java/com/learning/chat/log/*.java
git commit -m "feat: add chat session and routing services"
```

### Task 4: Add Netty handlers

**Files:**
- Create: `chat-server/src/main/java/com/learning/chat/handler/LoginHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/HeartbeatHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/PrivateMessageHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/GroupMessageHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/LogoutHandler.java`
- Create: `chat-server/src/main/java/com/learning/chat/handler/ExceptionHandler.java`

- [ ] **Step 1: Implement login handler**

Login should bind the `userId` to the current channel and send an ACK packet.

- [ ] **Step 2: Implement heartbeat handler**

Heartbeat should refresh `lastActiveTime` and not touch business storage.

- [ ] **Step 3: Implement private and group message handlers**

Private message:
- use `toUserId -> Channel`
- if online, send immediately
- if offline, return failure result and log it

Group message:
- use `groupId -> ChannelGroup`
- broadcast to all members except sender if desired

- [ ] **Step 4: Implement logout and exception handlers**

Logout should unbind the session. Exception handler should log and return an `ERROR` packet.

- [ ] **Step 5: Run compile check**

Run: `mvn -pl chat-server -am compile`
Expected: handler classes compile.

- [ ] **Step 6: Commit**

```bash
git add chat-server/src/main/java/com/learning/chat/handler/*.java
git commit -m "feat: add chat server handlers"
```

### Task 5: Add chat-server documentation and project links

**Files:**
- Create: `chat-server/README.md`
- Modify: `README.md`

- [ ] **Step 1: Write `chat-server/README.md`**

Include:
- overview
- supported packet types
- port `9000`
- login / heartbeat / private / group / logout examples
- note that phase-2 items are in `docs/future-features/chat-server-phase-2.md`

- [ ] **Step 2: Update root README**

Add Chat Server to progress:

```markdown
- ✅ Chat server (Netty) - 100% complete
  - ✅ Netty bootstrap
  - ✅ packet models
  - ✅ codec
  - ✅ session manager
  - ✅ routing service
  - ✅ handlers
  - ✅ README.md
```

Add the module tree entry for `chat-server/` as completed.

Add documentation links:

```markdown
- [Chat Server README](chat-server/README.md)
- [Future Features Index](docs/future-features/README.md)
```

- [ ] **Step 3: Commit**

```bash
git add chat-server/README.md README.md
git commit -m "docs: add chat server documentation"
```

### Task 6: Verify startup and basic packet flow

**Files:**
- Test: `chat-server/src/main/java/com/learning/chat/bootstrap/ChatServerBootstrap.java`
- Test: `chat-server/src/main/java/com/learning/chat/handler/*.java`
- Test: `chat-server/src/main/java/com/learning/chat/codec/*.java`

- [ ] **Step 1: Start the chat server**

Run:
```bash
mvn -pl chat-server spring-boot:run
```

Expected:
- logs show the server bound to `9000`

- [ ] **Step 2: Send a login packet**

Use a simple client or netcat-compatible test to send a JSON login packet and verify the session is bound.

- [ ] **Step 3: Send private and group messages**

Verify:
- online user receives the private message
- group members receive the broadcast

- [ ] **Step 4: Commit**

```bash
git add chat-server/src/main/java/com/learning/chat/bootstrap/ChatServerBootstrap.java
git commit -m "test: verify chat server startup and packet flow"
```

## Self-Review

### Spec coverage
- Netty startup: Task 1
- packet protocol: Task 2
- session/routing: Task 3
- handlers: Task 4
- docs and repo links: Task 5
- startup verification: Task 6
- phase-2 backlog: already captured in `docs/future-features/chat-server-phase-2.md`

### Placeholder scan
- No TBD placeholders
- No incomplete file references
- No unresolved task names

### Type consistency
- `ChatPacketType` is the source of truth for message types
- `ChatSession` uses `Channel` as the transport handle
- `DeliveryResult` is used for both private and group delivery reporting
- `ChatRoutingService` method names match handler expectations
