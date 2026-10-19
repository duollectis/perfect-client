package com.perfect.client.functional.function.player.packetMine.data;

import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Getter
public class RebreakInfo extends ClientBreakInfo {

	private final boolean airPreBreak;

	public RebreakInfo(ClientBreakInfo info, boolean airPreBreak) {
		this(info.getPos(), info.getFacing(), info.getFunctionId(), airPreBreak);
	}

	private RebreakInfo(
			BlockPos pos,
			Direction facing,
			String functionId,
			boolean airPreBreak
	) {
		super(pos, facing, 0, functionId);
		this.airPreBreak = airPreBreak;

		// TODO: заменить на setRebreak
		setClickedLast(true);
	}
}
