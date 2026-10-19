package com.perfect.client.core.discord.system;

import com.google.gson.JsonObject;

public record Packet(Opcode opcode, JsonObject data) {
}