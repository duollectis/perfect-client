package com.perfect.client.functional.function.movement.phase;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.functional.function.movement.phase.modes.PearlMode;
import com.perfect.client.functional.function.movement.phase.utils.PhaseMode;
import lombok.Getter;

public class Phase extends Function {

	private final PhaseConfig config = new PhaseConfig(this);

	@Getter
	private final PearlMode pearlMode = new PearlMode(this, config);

	@Getter
	private final ModeContainer<Mode, PhaseMode> modeHandler = createModeContainer(config::getMode)
			.add(Mode.PEARL, pearlMode);

	public Phase() {
		super("phase", "Phase", Category.MOVEMENT);
	}

	public enum Mode {
		PEARL
	}
}
