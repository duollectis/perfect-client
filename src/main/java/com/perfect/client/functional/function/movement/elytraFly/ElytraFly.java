package com.perfect.client.functional.function.movement.elytraFly;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.movement.elytraFly.modes.BoostMode;
import com.perfect.client.functional.function.movement.elytraFly.modes.BounceMode;
import com.perfect.client.functional.function.movement.elytraFly.utils.FlyMode;
import lombok.Getter;


public class ElytraFly extends Function {

	private final ElytraFlyConfig config = new ElytraFlyConfig(this);

	@Getter
	private final ModeContainer<Mode, FlyMode> modeHandler = createModeContainer(config::getMode)
			.add(Mode.BOOST, new BoostMode(this, config))
			.add(Mode.BOUNCE, new BounceMode(this, config))
			.add(Mode.CONTROL, new FlyMode(this, config));

	@Getter
	private boolean boostBindActive;

	public ElytraFly() {
		super("elytra-fly", "Elytra Fly", Category.MOVEMENT, "Elytra Bounce", "Elytra Boost");
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	public boolean isActive(Mode mode) {
		return isEnabled() && config.getMode().is(mode);
	}

	public boolean needNoRotate() {
		return isEnabled()
				&& modeHandler.getCurrentMode() instanceof BoostMode boostMode
				&& boostMode.isStopped()
				&& config.getStopBypass().get();
	}

	public void switchBoost(Bind ignoredBind, InputEvent event) {
		if (event.isPress()) {
			boostBindActive = true;
		}
		else if (event.isRelease()) {
			boostBindActive = false;
		}
	}

	public enum Mode {
		BOUNCE,
		CONTROL,
		BOOST
	}
}
