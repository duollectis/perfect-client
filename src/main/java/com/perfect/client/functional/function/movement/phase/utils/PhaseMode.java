package com.perfect.client.functional.function.movement.phase.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.movement.phase.Phase;
import com.perfect.client.functional.function.movement.phase.PhaseConfig;

public abstract class PhaseMode extends FunctionHandler<Phase, PhaseConfig> {

	public PhaseMode(Phase function, PhaseConfig config) {
		super(function, config);
	}
}
