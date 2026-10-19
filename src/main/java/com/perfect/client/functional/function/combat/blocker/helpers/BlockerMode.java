package com.perfect.client.functional.function.combat.blocker.helpers;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.blocker.Blocker;
import com.perfect.client.functional.function.combat.blocker.BlockerConfig;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract class BlockerMode extends FunctionHandler<Blocker, BlockerConfig> {

	public BlockerMode(Blocker function, BlockerConfig config) {
		super(function, config);
	}

	public void onTick() {}

	public abstract void addTrap(List<BlockPos> list);
}
