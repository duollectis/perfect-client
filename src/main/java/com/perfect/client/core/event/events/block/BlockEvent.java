package com.perfect.client.core.event.events.block;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Getter
@AllArgsConstructor

public class BlockEvent extends Cancellable {

	private final BlockPos pos;
	private final Direction direction;
}
