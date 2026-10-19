package com.perfect.client.core.event.events.function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor

public class PacketMineEvent {

	private final BlockPos pos;
}
