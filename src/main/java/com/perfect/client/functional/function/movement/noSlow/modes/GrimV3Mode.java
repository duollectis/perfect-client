package com.perfect.client.functional.function.movement.noSlow.modes;

import com.perfect.client.functional.function.movement.noSlow.NoSlow;
import com.perfect.client.functional.function.movement.noSlow.NoSlowConfig;
import com.perfect.client.functional.function.movement.noSlow.utils.NoSlowMode;


public class GrimV3Mode extends NoSlowMode {

	private int tickCounter = 0;

	public GrimV3Mode(
			NoSlow function,
			NoSlowConfig config
	) {
		super(function, config);
	}

	
	@Override
	public void onPreTick() {
		if (mc.player == null || mc.player.isGliding()) {
			return;
		}

		if (mc.player.isUsingItem()) {
			tickCounter++;
		}
		else {
			tickCounter = 0;
		}
	}

	@Override
	public boolean canNoItemSlowDown() {
		if (tickCounter >= 2) {
			tickCounter = 0;
			return true;
		}
		return false;
	}
}
