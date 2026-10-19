package com.perfect.client.functional.function.player.packetMine.data;

import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import lombok.Getter;

@Getter
public class TimedSwapInfo {

	private final SwapInfo swap;

	private int ticksPost = 0;

	private boolean mined = false;

	public TimedSwapInfo(SwapInfo swap) {
		this.swap = swap;
	}

	public void tick() {
		ticksPost++;
	}

	public void setMined() {
		mined = true;
	}
}
