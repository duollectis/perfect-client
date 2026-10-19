package com.perfect.client.functional.function.movement.step.modes;

import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.functional.function.movement.step.Step;
import com.perfect.client.functional.function.movement.step.StepConfig;
import com.perfect.client.functional.function.movement.step.utils.StepMode;

import static com.perfect.client.core.utils.game.entity.PlayerUtils.setStep;


public class NormalMode extends StepMode {

	public NormalMode(Step function, StepConfig config) {
		super(function, config);
	}

	@Override
	public void onDisable() {
		setStep(0.6);
	}

	
	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		if (canStep(true)) {
			setStep(config.getHeight().get());
		}
		else {
			setStep(0.6);
		}
	}
}
