package com.perfect.client.core.event.events.network;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.packet.Packet;

@Getter
@AllArgsConstructor

public class PacketEvent extends Cancellable {

	private final Packet<?> packet;
}
