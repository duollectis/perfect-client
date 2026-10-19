package com.perfect.client.core.event.events.block;

import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor

public class BlockUpdateEvent {

	private final BlockPos pos;
	private final BlockState oldState;
	private final BlockState newState;

	public boolean isBreak() {
		return BlockUtils.isBlock(oldState) && !BlockUtils.isBlock(newState);
	}

	public boolean isCombatBreak() {
		return isBreak() && BlockUtils.isBlastResist(oldState);
	}

	public boolean isPlace() {
		return !BlockUtils.isBlock(oldState) && BlockUtils.isBlock(newState);
	}

	public boolean isInRange(double range) {
		return !PositionMath.isOutOfRange(pos, range);
	}
}
