package com.perfect.client.functional.function.movement.autoSprint;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;

public class AutoSprint extends Function {

	public AutoSprint() {
		super("auto-sprint", "Auto Sprint", Category.MOVEMENT);
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.player == null) {
			return;
		}
		event.sprinting.set(true);
	}
}
