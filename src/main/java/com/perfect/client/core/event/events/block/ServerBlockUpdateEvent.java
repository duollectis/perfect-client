package com.perfect.client.core.event.events.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor

public class ServerBlockUpdateEvent {

	private final BlockPos pos;
	private final BlockState state;
}
