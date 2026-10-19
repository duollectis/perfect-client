package com.perfect.client.functional.function.movement.safeWalk;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.functional.function.movement.safeWalk.modes.LegitMode;
import com.perfect.client.functional.function.movement.safeWalk.modes.NormalMode;
import com.perfect.client.functional.function.movement.safeWalk.utils.SafeWalkMode;
import lombok.Getter;

public class SafeWalk extends Function {

	private final SafeWalkConfig config = new SafeWalkConfig(this);

	@Getter
	private final ModeContainer<Mode, SafeWalkMode> modeContainer = createModeContainer(config::getMode)
			.add(Mode.NORMAL, new NormalMode(this, config))
			.add(Mode.LEGIT, new LegitMode(this, config));

	public SafeWalk() {
		super("safe-walk", "Safe Walk", Category.MOVEMENT, "Sneak", "Shift", "Bridging");
	}

	public void updateSneakDelay() {
		modeContainer.getCurrentMode().updateSneakDelay();
	}

	public enum Mode {
		NORMAL,
		LEGIT
	}
}
