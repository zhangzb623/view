# Chat Server TCP Test Script Guide

本文档提供一个最小可用的 TCP 测试方式，方便你直接验证 `chat-server` 一期能力。

## 1. 适用场景

如果你现在还没有前端，也没有专门的 Netty 客户端，可以先用一个简单脚本完成：
- 登录
- 心跳
- 单聊
- 群聊
- 已读回执
- 登出

## 2. 协议提醒

`chat-server` 当前使用的是：

- 4 字节长度前缀
- JSON 消息体

所以测试脚本必须：
1. 先把 JSON 转成 UTF-8 字节
2. 再在前面拼一个 4 字节大端长度
3. 最后通过 TCP socket 发出去

## 3. Python 测试脚本

你可以在本地新建一个 `chat_test_client.py`，内容如下：

```python
import json
import socket
import struct
import threading
import time


def encode_packet(packet: dict) -> bytes:
    body = json.dumps(packet, ensure_ascii=False).encode("utf-8")
    return struct.pack(">I", len(body)) + body


class ChatClient:
    def __init__(self, host: str, port: int, name: str):
        self.host = host
        self.port = port
        self.name = name
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))
        self.running = True
        self.reader = threading.Thread(target=self.read_loop, daemon=True)
        self.reader.start()

    def send(self, packet: dict):
        print(f"[{self.name}] SEND => {packet}")
        self.sock.sendall(encode_packet(packet))

    def read_loop(self):
        try:
            while self.running:
                header = self.recv_exact(4)
                if not header:
                    break
                length = struct.unpack(">I", header)[0]
                body = self.recv_exact(length)
                if not body:
                    break
                msg = json.loads(body.decode("utf-8"))
                print(f"[{self.name}] RECV <= {msg}")
        except Exception as e:
            print(f"[{self.name}] read error: {e}")

    def recv_exact(self, n: int):
        data = b""
        while len(data) < n:
            chunk = self.sock.recv(n - len(data))
            if not chunk:
                return None
            data += chunk
        return data

    def close(self):
        self.running = False
        try:
            self.sock.close()
        except Exception:
            pass


def main():
    client1 = ChatClient("127.0.0.1", 9000, "alice")
    client2 = ChatClient("127.0.0.1", 9000, "bob")
    client3 = ChatClient("127.0.0.1", 9000, "carol")

    time.sleep(1)

    client1.send({
        "type": "LOGIN",
        "requestId": "req-login-1001",
        "messageId": "msg-login-1001",
        "fromUserId": 1001,
        "nickname": "alice"
    })
    client2.send({
        "type": "LOGIN",
        "requestId": "req-login-1002",
        "messageId": "msg-login-1002",
        "fromUserId": 1002,
        "nickname": "bob"
    })
    client3.send({
        "type": "LOGIN",
        "requestId": "req-login-1003",
        "messageId": "msg-login-1003",
        "fromUserId": 1003,
        "nickname": "carol"
    })

    time.sleep(1)

    client1.send({
        "type": "HEARTBEAT",
        "requestId": "req-heartbeat-1001",
        "messageId": "msg-heartbeat-1001",
        "fromUserId": 1001,
        "nickname": "alice"
    })

    time.sleep(1)

    client1.send({
        "type": "PRIVATE_MESSAGE",
        "requestId": "req-pm-001",
        "messageId": "msg-pm-001",
        "fromUserId": 1001,
        "toUserId": 1002,
        "nickname": "alice",
        "content": "hello bob"
    })

    time.sleep(1)

    client1.send({
        "type": "GROUP_MESSAGE",
        "requestId": "req-group-001",
        "messageId": "msg-group-001",
        "fromUserId": 1001,
        "groupId": 2001,
        "groupMemberIds": [1001, 1002, 1003],
        "nickname": "alice",
        "content": "hello group"
    })

    time.sleep(1)

    client2.send({
        "type": "READ_ACK",
        "requestId": "req-read-001",
        "messageId": "msg-pm-001",
        "fromUserId": 1002,
        "toUserId": 1001
    })

    time.sleep(1)

    client1.send({
        "type": "LOGOUT",
        "requestId": "req-logout-001",
        "messageId": "msg-logout-001",
        "fromUserId": 1001
    })

    time.sleep(3)

    client1.close()
    client2.close()
    client3.close()


if __name__ == "__main__":
    main()
```

## 4. 怎么运行

先启动服务端：

```bash
mvn -pl chat-server spring-boot:run
```

再运行脚本：

```bash
python chat_test_client.py
```

如果你电脑上是 Python 3，也可能需要：

```bash
python3 chat_test_client.py
```

## 5. 运行后你应该看到什么

### alice / bob / carol 登录
你会看到每个客户端打印各自的 `LOGIN` 成功响应。

### alice 发心跳
你会看到 `HEARTBEAT` 成功响应。

### alice 发单聊给 bob
你应该同时看到：
- `alice` 收到单聊 ack
- `bob` 收到原始 `PRIVATE_MESSAGE`

### alice 发群聊到 group 2001
你应该同时看到：
- `alice` 收到群聊 ack
- `bob` 和 `carol` 收到原始 `GROUP_MESSAGE`

### bob 发已读回执
你会看到 `bob` 收到 `READ_ACK` 成功响应。

### alice 登出
你会看到 `alice` 收到 `LOGOUT` 成功响应，随后连接关闭。

## 6. 如何验证错误场景

你可以把脚本里的消息改一改，验证错误回包。

### 1）缺失 `fromUserId`

把登录改成：

```python
client1.send({
    "type": "LOGIN",
    "requestId": "req-login-err-001",
    "messageId": "msg-login-err-001",
    "nickname": "alice"
})
```

预期：
- 返回 `ERROR`
- `reason = fromUserId missing`

### 2）单聊目标用户不在线

把单聊改成发给一个未登录用户：

```python
client1.send({
    "type": "PRIVATE_MESSAGE",
    "requestId": "req-pm-offline-001",
    "messageId": "msg-pm-offline-001",
    "fromUserId": 1001,
    "toUserId": 9999,
    "nickname": "alice",
    "content": "hello offline user"
})
```

预期：
- 返回 `ERROR`
- `reason = target user offline`
- `deliveredTo = "9999"`
- `deliveredCount = 0`

### 3）给自己发单聊

```python
client1.send({
    "type": "PRIVATE_MESSAGE",
    "requestId": "req-pm-self-001",
    "messageId": "msg-pm-self-001",
    "fromUserId": 1001,
    "toUserId": 1001,
    "nickname": "alice",
    "content": "hello self"
})
```

预期：
- 返回 `ERROR`
- `reason = cannot send private message to self`

### 4）群里没有其他在线成员

```python
client1.send({
    "type": "GROUP_MESSAGE",
    "requestId": "req-group-empty-001",
    "messageId": "msg-group-empty-001",
    "fromUserId": 1001,
    "groupId": 3001,
    "groupMemberIds": [1001],
    "nickname": "alice",
    "content": "only me"
})
```

预期：
- 返回 `ERROR`
- `reason = group has no other active members`
- `deliveredTo = "3001"`
- `deliveredCount = 0`

### 5）已读回执缺失 `messageId`

```python
client2.send({
    "type": "READ_ACK",
    "requestId": "req-read-err-001",
    "fromUserId": 1002,
    "toUserId": 1001
})
```

预期：
- 返回 `ERROR`
- `reason = fromUserId, toUserId or messageId missing`

## 7. 建议你怎么复盘

建议你按下面顺序看结果：

1. 先看客户端打印出的发送/接收数据
2. 再对照 `chat-server/PROTOCOL.md` 看字段含义
3. 再对照 `chat-server/TESTING.md` 看每步预期行为
4. 最后去看服务端日志，确认 handler 行为是否一致

## 8. 当前限制

这个脚本只是临时测试工具，适合当前一期学习与验证。

它还不具备：
- 命令行参数化
- 自动断言
- 批量回归
- GUI 界面
- WebSocket 适配

如果后面你要把测试体验做得更像生产工具，这部分已经放进二期文档。
