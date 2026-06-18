# Chat Server Protocol Guide

本文档用于说明 `chat-server` 当前一期支持哪些消息类型、每种消息该怎么调用、以及服务端会触发哪些行为。

## 1. 总览

当前一期支持的消息类型：

- `LOGIN`
- `HEARTBEAT`
- `PRIVATE_MESSAGE`
- `GROUP_MESSAGE`
- `READ_ACK`
- `LOGOUT`
- `ERROR`

协议模型统一使用 `ChatPacket`。

## 2. 传输协议

传输格式不是 HTTP，而是 TCP 自定义二进制帧：

- `4 byte length`
- `JSON body`

其中：
- `length` 表示后面 JSON 消息体的字节长度
- `body` 是 UTF-8 编码的 JSON

## 3. ChatPacket 字段说明

常用字段如下：

| 字段 | 含义 |
| --- | --- |
| `type` | 消息类型 |
| `requestId` | 请求链路标识，用于把请求和响应关联起来 |
| `messageId` | 消息唯一标识 |
| `fromUserId` | 发送方用户 ID |
| `toUserId` | 接收方用户 ID，单聊和读回执使用 |
| `groupId` | 群 ID，群聊使用 |
| `groupMemberIds` | 群成员 ID 列表，群聊请求时可携带 |
| `nickname` | 用户昵称 |
| `content` | 文本消息内容 |
| `success` | 是否处理成功 |
| `reason` | 处理结果说明 |
| `deliveredTo` | 投递目标，单聊通常是用户 ID，群聊通常是群 ID |
| `deliveredCount` | 实际投递数量 |
| `timestamp` | 服务端生成的响应时间 |
| `ackTimestamp` | 已读回执确认时间 |

## 4. LOGIN

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

### 服务端触发行为

- 命中 `LoginHandler`
- 校验 `fromUserId`
- 把用户和当前连接绑定到会话管理器
- 记录登录日志
- 返回 `LOGIN` 成功响应

### 成功响应

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

---

## 5. HEARTBEAT

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

### 服务端触发行为

- 命中 `HeartbeatHandler`
- 校验 `fromUserId`
- 刷新当前会话活跃时间
- 记录心跳日志
- 返回 `HEARTBEAT` 成功响应

### 成功响应

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

## 6. PRIVATE_MESSAGE

### 请求示例

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

### 服务端触发行为

- 命中 `PrivateMessageHandler`
- 校验单聊必要字段
- 调用路由服务查找 `toUserId` 对应在线连接
- 若在线，则把原始消息投递给目标用户
- 给发送者返回单聊 ack
- 记录消息日志

### 发送方成功响应

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

### 接收方收到的内容

目标用户收到的是原始业务消息：

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

---

## 7. GROUP_MESSAGE

### 请求示例

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

### 服务端触发行为

- 命中 `GroupMessageHandler`
- 校验群聊必要字段
- 根据 `groupMemberIds` 组装在线群成员
- 把在线成员加入 `ChannelGroup`
- 过滤掉发送者本人，只广播给其他在线成员
- 给发送者返回群聊 ack
- 记录消息日志

### 发送方成功响应

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

### 接收方收到的内容

群成员收到的是原始群消息：

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

---

## 8. READ_ACK

### 请求示例

```json
{
  "type": "READ_ACK",
  "requestId": "req-read-001",
  "messageId": "msg-pm-001",
  "fromUserId": 1002,
  "toUserId": 1001
}
```

### 服务端触发行为

- 命中 `ReadAckHandler`
- 校验 `fromUserId`、`toUserId`、`messageId`
- 记录已读回执日志
- 返回读回执确认响应

### 成功响应

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

---

## 9. LOGOUT

### 请求示例

```json
{
  "type": "LOGOUT",
  "requestId": "req-logout-001",
  "messageId": "msg-logout-001",
  "fromUserId": 1001
}
```

### 服务端触发行为

- 命中 `LogoutHandler`
- 查找当前连接的用户信息
- 记录登出日志
- 清理连接和用户绑定关系
- 返回 `LOGOUT` 成功响应
- 主动关闭当前连接

### 成功响应

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

## 10. ERROR

当请求参数不合法，或者投递条件不满足时，服务端会返回 `ERROR`。

常见错误包括：
- `fromUserId missing`
- `fromUserId, toUserId or content missing`
- `target user offline`
- `cannot send private message to self`
- `fromUserId, groupId or content missing`
- `group empty`
- `group has no other active members`
- `fromUserId, toUserId or messageId missing`

示例：

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

## 11. 当前一期边界

当前协议已经支持基础通信闭环，但以下仍不在一期范围内：
- 离线消息存储与补投
- 历史消息查询
- 群成员持久化管理
- 前端页面
- 文件与图片传输
- 多实例同步
- Redis / MongoDB 持久化协同

这些内容统一记录在：

- `docs/future-features/chat-server-phase-2.md`
