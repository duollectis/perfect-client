package com.perfect.client.functional.function.combat.autoCrystal.handlers;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;

public class BlockBreakHandler extends FunctionHandler<AutoCrystal, AutoCrystalConfig> {

	public BlockBreakHandler(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (isBreakBlockEvent(event)) {
			function.autoCrystalTick();
			function.sendActions();
		}
	}

	private boolean isBreakBlockEvent(BlockUpdateEvent event) {
		if (!event.isCombatBreak()) {
			return false;
		}
		return event.isInRange(config.getTargetRange().get());
	}
}
