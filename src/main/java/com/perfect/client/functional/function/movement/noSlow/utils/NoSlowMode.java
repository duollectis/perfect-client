package com.perfect.client.functional.function.movement.noSlow.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.movement.noSlow.NoSlow;
import com.perfect.client.functional.function.movement.noSlow.NoSlowConfig;

public class NoSlowMode extends FunctionHandler<NoSlow, NoSlowConfig> {

	public NoSlowMode(NoSlow function, NoSlowConfig config) {
		super(function, config);
	}

	public boolean canNoItemSlowDown() {
		return true;
	}
}
