# Chat Server Testing Guide

本文档用于帮助你在 `chat-server` 一期范围内，按顺序验证各项功能是否工作正常。

## 1. 测试目标

当前一期需要重点验证：
- 连接与登录
- 心跳保活
- 单聊消息投递
- 群聊消息广播
- 已读回执
- 登出
- 错误回包
- 长度前缀 + JSON 协议

## 2. 启动服务

在项目根目录执行：

```bash
mvn -pl chat-server spring-boot:run
```

默认监听端口：

- `9000`

## 3. 协议说明

Chat Server 当前不是 HTTP 接口，而是基于 TCP 的自定义协议：

- 前 4 个字节：消息体长度（大端整数）
- 后续 N 个字节：JSON 字符串

也就是说，不能直接用 Postman 按 HTTP 调用。

你可以用以下方式测试：
- 自己写一个简单 TCP 客户端
- 用 Netty / Java socket 写 demo 客户端
- 用 Python socket 脚本发送二进制报文

## 4. 推荐测试顺序

建议按下面顺序测试，前一个通过后再测后一个：

1. 登录
2. 心跳
3. 单聊
4. 群聊
5. 已读回执
6. 登出
7. 错误场景

---

## 5. 登录测试

### 请求示例

```json
{
  "type": "LOGIN",
  "requestId": "req-login-1001",
  "messageId": "msg-login-1001",
  "fromUserId": 1001,
  "nickname": "alice"
}
```

### 会触发什么

服务端会：
- 进入 `LoginHandler`
- 校验 `fromUserId` 是否存在
- 调用 `SessionManager.bind(...)` 绑定用户和当前连接
- 记录登录日志
- 返回登录成功回包

### 成功回包示例

```json
{
  "type": "LOGIN",
  "requestId": "req-login-1001",
  "messageId": "msg-login-1001",
  "fromUserId": 1001,
  "nickname": "alice",
  "success": true,
  "reason": "login success",
  "timestamp": 1710000000001
}
```

### 失败场景

如果 `fromUserId` 缺失，会返回：

```json
{
  "type": "ERROR",
  "requestId": "req-login-1001",
  "messageId": "msg-login-1001",
  "fromUserId": null,
  "success": false,
  "reason": "fromUserId missing",
  "timestamp": 1710000000001
}
```

---

## 6. 心跳测试

### 请求示例

```json
{
  "type": "HEARTBEAT",
  "requestId": "req-heartbeat-1001",
  "messageId": "msg-heartbeat-1001",
  "fromUserId": 1001,
  "nickname": "alice"
}
```

### 会触发什么

服务端会：
- 进入 `HeartbeatHandler`
- 校验 `fromUserId`
- 再次绑定当前连接上的用户信息
- 刷新会话活跃时间
- 记录心跳日志
- 返回心跳成功回包

### 成功回包示例

```json
{
  "type": "HEARTBEAT",
  "requestId": "req-heartbeat-1001",
  "messageId": "msg-heartbeat-1001",
  "fromUserId": 1001,
  "success": true,
  "reason": "heartbeat ok",
  "timestamp": 1710000000100
}
```

---

## 7. 单聊测试

### 测试前提

需要至少两个客户端连接：
- 用户 `1001`
- 用户 `1002`

并且二者都已经先发送过 `LOGIN`。

### 请求示例

由 `1001` 发给 `1002`：

```json
{
  "type": "PRIVATE_MESSAGE",
  "requestId": "req-pm-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "nickname": "alice",
  "content": "hello bob"
}
```

### 会触发什么

服务端会：
- 进入 `PrivateMessageHandler`
- 校验 `fromUserId`、`toUserId`、`content`
- 调用 `ChatRoutingService.sendPrivateMessage(...)`
- 根据 `toUserId` 查找目标在线连接
- 如果目标在线，则把原消息写给目标连接
- 记录消息日志
- 给发送方返回 ack 回包

### 发送方成功回包示例

```json
{
  "type": "PRIVATE_MESSAGE",
  "requestId": "req-pm-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "content": "hello bob",
  "success": true,
  "reason": "delivered",
  "deliveredTo": "1002",
  "deliveredCount": 1,
  "timestamp": 1710000000200
}
```

### 接收方会收到什么

接收方 `1002` 会直接收到发送方原始消息包，也就是：

```json
{
  "type": "PRIVATE_MESSAGE",
  "requestId": "req-pm-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "nickname": "alice",
  "content": "hello bob"
}
```

### 常见失败场景

#### 目标用户不在线

```json
{
  "type": "ERROR",
  "requestId": "req-pm-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "success": false,
  "reason": "target user offline",
  "deliveredTo": "1002",
  "deliveredCount": 0,
  "timestamp": 1710000000200
}
```

#### 给自己发单聊

```json
{
  "type": "ERROR",
  "requestId": "req-pm-self-001",
  "messageId": "msg-pm-self-001",
  "fromUserId": 1001,
  "toUserId": 1001,
  "success": false,
  "reason": "cannot send private message to self",
  "deliveredTo": "1001",
  "deliveredCount": 0,
  "timestamp": 1710000000200
}
```

---

## 8. 群聊测试

### 测试前提

建议准备三个客户端：
- `1001`
- `1002`
- `1003`

都先完成登录。

### 请求示例

由 `1001` 发群聊消息到群 `2001`：

```json
{
  "type": "GROUP_MESSAGE",
  "requestId": "req-group-001",
  "messageId": "msg-group-001",
  "fromUserId": 1001,
  "groupId": 2001,
  "groupMemberIds": [1001, 1002, 1003],
  "nickname": "alice",
  "content": "hello group"
}
```

### 会触发什么

服务端会：
- 进入 `GroupMessageHandler`
- 校验 `fromUserId`、`groupId`、`content`
- 调用 `ChatRoutingService.sendGroupMessage(...)`
- 根据 `groupMemberIds` 找到在线成员并加入群组映射
- 把发送者自己也加入该群的 ChannelGroup
- 向除发送者之外的在线成员广播消息
- 记录消息日志
- 给发送方返回群聊 ack 回包

### 发送方成功回包示例

```json
{
  "type": "GROUP_MESSAGE",
  "requestId": "req-group-001",
  "messageId": "msg-group-001",
  "fromUserId": 1001,
  "groupId": 2001,
  "success": true,
  "reason": "broadcasted",
  "deliveredTo": "2001",
  "deliveredCount": 2,
  "timestamp": 1710000000300
}
```

### 接收方会收到什么

群成员 `1002`、`1003` 会收到原始群消息包：

```json
{
  "type": "GROUP_MESSAGE",
  "requestId": "req-group-001",
  "messageId": "msg-group-001",
  "fromUserId": 1001,
  "groupId": 2001,
  "groupMemberIds": [1001, 1002, 1003],
  "nickname": "alice",
  "content": "hello group"
}
```

### 常见失败场景

#### 群里没有其他在线成员

```json
{
  "type": "ERROR",
  "requestId": "req-group-001",
  "messageId": "msg-group-001",
  "fromUserId": 1001,
  "groupId": 2001,
  "success": false,
  "reason": "group has no other active members",
  "deliveredTo": "2001",
  "deliveredCount": 0,
  "timestamp": 1710000000300
}
```

#### `groupId` 缺失或内容为空

会返回 `ERROR`，原因通常是：
- `fromUserId, groupId or content missing`
- `group id missing`

---

## 9. 已读回执测试

### 测试前提

先完成一条单聊消息投递，例如 `1001 -> 1002`，消息号为 `msg-pm-001`。

### 请求示例

由 `1002` 回给服务端：

```json
{
  "type": "READ_ACK",
  "requestId": "req-read-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1002,
  "toUserId": 1001
}
```

### 会触发什么

服务端会：
- 进入 `ReadAckHandler`
- 校验 `fromUserId`、`toUserId`、`messageId`
- 记录已读回执日志
- 返回已读确认成功回包

### 成功回包示例

```json
{
  "type": "READ_ACK",
  "requestId": "req-read-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1002,
  "toUserId": 1001,
  "success": true,
  "reason": "read ack received",
  "timestamp": 1710000000400,
  "ackTimestamp": 1710000000400
}
```

### 当前边界

当前一期里，已读回执只做：
- 参数校验
- 服务端日志记录
- 成功/失败回包

当前不会做：
- 未读数聚合
- 群已读统计
- 消息状态持久化

---

## 10. 登出测试

### 请求示例

```json
{
  "type": "LOGOUT",
  "requestId": "req-logout-001",
  "messageId": "msg-logout-001",
  "fromUserId": 1001
}
```

### 会触发什么

服务端会：
- 进入 `LogoutHandler`
- 识别当前连接对应的用户
- 记录登出日志
- 调用 `SessionManager.unbind(...)` 解除绑定
- 返回登出成功回包
- 随后主动关闭连接

### 成功回包示例

```json
{
  "type": "LOGOUT",
  "requestId": "req-logout-001",
  "fromUserId": 1001,
  "success": true,
  "reason": "logout success",
  "timestamp": 1710000000500
}
```

---

## 11. 错误场景建议清单

建议你专门测下面这些异常输入：

### 登录
- `fromUserId` 缺失

### 心跳
- `fromUserId` 缺失

### 单聊
- `toUserId` 缺失
- `content` 为空
- 目标用户未登录
- 给自己发消息

### 群聊
- `groupId` 缺失
- `content` 为空
- `groupMemberIds` 全部不在线
- 只有发送者自己在线

### 已读回执
- `messageId` 缺失
- `toUserId` 缺失

### 登出
- 已登录后正常登出
- 直接断开连接，观察 `channelInactive` 清理行为

---

## 12. 调试时你应该重点观察什么

### 1）发送方回包
重点看这些字段：
- `type`
- `requestId`
- `messageId`
- `success`
- `reason`
- `deliveredTo`
- `deliveredCount`
- `ackTimestamp`

### 2）接收方是否真的收到了消息
- 单聊：目标用户是否收到原始消息
- 群聊：除发送者外的群成员是否收到原始消息

### 3）日志输出
当前服务会记录：
- 登录日志
- 登出日志
- 心跳日志
- 消息日志
- 已读回执日志

### 4）连接状态
- 重复登录时，新连接是否覆盖旧连接
- 登出后是否无法继续收消息

---

## 13. 一期与二期边界

当前文档覆盖的是一期可验证能力。

以下内容仍然属于二期：
- WebSocket 前端页面
- Redis 会话共享
- 离线消息
- 历史消息查询
- 文件/图片传输
- 群成员管理后台
- 多实例同步
- MongoDB 消息持久化

详见：

- `docs/future-features/chat-server-phase-2.md`
