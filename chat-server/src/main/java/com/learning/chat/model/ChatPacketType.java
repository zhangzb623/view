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
