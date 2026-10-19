package com.perfect.client.functional.function.movement.safeWalk.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalk;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalkConfig;

public class SafeWalkMode extends FunctionHandler<SafeWalk, SafeWalkConfig> {

	public SafeWalkMode(SafeWalk function, SafeWalkConfig config) {
		super(function, config);
	}

	public void updateSneakDelay() {}
}
