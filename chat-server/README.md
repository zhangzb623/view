# Chat Server

Netty 聊天服务器，一期范围支持：登录、心跳、单聊、群聊、已读回执、下线和基础日志。

## 当前一期范围
- Netty 服务启动
- 登录 / 下线
- 心跳保活
- 单聊路由
- 群聊广播
- 已读回执
- 基础日志输出
- 请求结果显式 ack / error 回包
- 基于长度前缀的 JSON 消息协议

## 默认端口
- `9000`

## 消息类型
- `LOGIN`
- `HEARTBEAT`
- `PRIVATE_MESSAGE`
- `GROUP_MESSAGE`
- `READ_ACK`
- `LOGOUT`
- `ERROR`

## 消息协议
- 采用 `4 byte length + JSON body` 的 TCP 帧格式
- `length` 为消息体字节长度，使用大端整数
- `body` 为 `ChatPacket` 的 JSON 序列化结果

## 消息包示例
```json
{
  "type": "PRIVATE_MESSAGE",
  "requestId": "req-001",
  "messageId": "msg-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "groupId": null,
  "nickname": "alice",
  "content": "hello",
  "success": null,
  "reason": null,
  "deliveredTo": null,
  "deliveredCount": null,
  "timestamp": 1710000000000,
  "ackTimestamp": null
}
```

## 成功回包示例
```json
{
  "type": "PRIVATE_MESSAGE",
  "requestId": "req-001",
  "messageId": "msg-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "success": true,
  "reason": "delivered",
  "deliveredTo": "1002",
  "deliveredCount": 1,
  "timestamp": 1710000000001,
  "ackTimestamp": null
}
```

## 失败回包示例
```json
{
  "type": "ERROR",
  "requestId": "req-001",
  "messageId": "msg-001",
  "fromUserId": 1001,
  "toUserId": 1002,
  "success": false,
  "reason": "target user offline",
  "deliveredTo": "1002",
  "deliveredCount": 0,
  "timestamp": 1710000000001,
  "ackTimestamp": null
}
```

## 已读回执示例
```json
{
  "type": "READ_ACK",
  "requestId": "req-002",
  "messageId": "msg-001",
  "fromUserId": 1002,
  "toUserId": 1001,
  "success": true,
  "reason": "read ack received",
  "deliveredTo": null,
  "deliveredCount": null,
  "timestamp": 1710000001000,
  "ackTimestamp": 1710000001000
}
```

## 项目结构
- `bootstrap/`：Netty 启动与生命周期管理
- `codec/`：消息编解码
- `handler/`：登录、心跳、单聊、群聊、已读回执、下线、异常处理
- `model/`：消息包、会话、投递结果等模型
- `service/`：消息路由服务
- `session/`：在线用户与群会话管理
- `log/`：聊天日志接口

## 启动
```bash
mvn -pl chat-server spring-boot:run
```

## 一期限制
- 当前为单节点内存态会话管理
- 同一用户重复登录时，新连接会覆盖旧连接映射，旧连接不再参与后续消息投递
- 群成员当前依赖请求内携带 `groupMemberIds` 动态装载在线成员，未接入持久化群成员关系
- 已读回执当前仅做接收确认与日志记录，未做持久化状态聚合
- 暂未提供离线消息
- 暂未提供历史消息查询
- 暂未提供前端页面
- 暂未提供文件与图片传输

## 二期后续功能
见：`docs/future-features/chat-server-phase-2.md`
