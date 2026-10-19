package com.perfect.client.functional.function.movement.step;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.movement.step.modes.NormalMode;
import com.perfect.client.functional.function.movement.step.modes.SlowMode;
import com.perfect.client.functional.function.movement.step.modes.StrictMode;
import com.perfect.client.functional.function.movement.step.utils.StepMode;
import lombok.Getter;

public class Step extends Function {

	private final StepConfig config = new StepConfig(this);

	@Getter
	private final ModeContainer<Mode, StepMode> modeContainer = createModeContainer(config::getMode)
			.add(Mode.NORMAL, new NormalMode(this, config))
			.add(Mode.STRICT, new StrictMode(this, config))
			.add(Mode.SLOW, new SlowMode(this, config));

	public Step() {
		super("step", "Step", Category.MOVEMENT);
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	public enum Mode {
		NORMAL,
		STRICT,
		SLOW
	}
}
