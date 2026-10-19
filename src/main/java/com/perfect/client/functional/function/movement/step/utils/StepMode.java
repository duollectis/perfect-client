package com.perfect.client.functional.function.movement.step.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.movement.step.Step;
import com.perfect.client.functional.function.movement.step.StepConfig;

public class StepMode extends FunctionHandler<Step, StepConfig> {

	public StepMode(Step function, StepConfig config) {
		super(function, config);
	}

	public boolean canStep(boolean checkGround) {
		if (mc.player == null) {
			return false;
		}

		if (config.getPauseOnShift().get() && mc.player.input.playerInput.sneak()) {
			return false;
		}

		if (mc.player.getAbilities().flying || mc.player.isRiding() || mc.player.isTouchingWater()) {
			return false;
		}

		if (mc.player.isGliding() || mc.player.isClimbing()) {
			return false;
		}

		if (checkGround && !mc.player.isOnGround()) {
			return false;
		}

		return !config.getPauseOnLagBack().get() || Managers.ANTI_CHEAT.passed(config.getPauseTime().get());
	}
}
