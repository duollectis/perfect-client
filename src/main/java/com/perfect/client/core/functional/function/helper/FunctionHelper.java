package com.perfect.client.core.functional.function.helper;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.utils.game.PacketUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;

public abstract class FunctionHelper<T extends Function, K extends FunctionConfig<?>> {

	protected static final MinecraftClient mc = MinecraftClient.getInstance();

	protected final T function;
	protected final K config;

	public FunctionHelper(T function, K config) {
		this.function = function;
		this.config = config;
	}

	protected static void sendPacket(Packet<?> packet) {
		PacketUtils.sendPacket(packet);
	}

	protected static void sendSequencedPacket(SequencedPacketCreator creator) {
		PacketUtils.sendSequencedPacket(creator);
	}
}
