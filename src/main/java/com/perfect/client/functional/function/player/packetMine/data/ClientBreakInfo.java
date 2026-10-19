package com.perfect.client.functional.function.player.packetMine.data;

import com.perfect.client.manager.game.mining.BreakInfo;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Getter
public class ClientBreakInfo extends BreakInfo {

	private final String functionId;
	private final Direction facing;

	@Setter
	private boolean clickedLast;

	@Setter
	private int ticks;

	public ClientBreakInfo(BlockPos pos, Direction facing, int priority, String functionId) {
		super(pos, priority);
		this.facing = facing;
		this.functionId = functionId;
	}

	public void tick() {
		ticks++;
	}

	public void reset() {
		ticks = 0;
	}
}
