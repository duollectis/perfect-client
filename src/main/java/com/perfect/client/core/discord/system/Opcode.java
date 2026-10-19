package com.perfect.client.core.discord.system;

public enum Opcode {
    HANDSHAKE,
    FRAME,
    CLOSE,
    PING,
    PONG;

    private static final Opcode[] VALUES = values();

    public static Opcode valueOf(int i) {
        return VALUES[i];
    }
}