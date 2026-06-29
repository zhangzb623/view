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
