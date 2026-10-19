package com.perfect.client.functional.function.movement.speed;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.movement.speed.modes.StrafeMode;
import com.perfect.client.functional.function.movement.speed.modes.VanillaMode;
import com.perfect.client.functional.function.movement.speed.utils.SpeedMode;
import lombok.Getter;

@Getter
public class Speed extends Function {

	private final SpeedConfig config = new SpeedConfig(this);

	private final ModeContainer<Mode, SpeedMode> modeHandler = createModeContainer(config::getMode)
			.add(Mode.VANILLA, new VanillaMode(this, config))
			.add(Mode.STRAFE, new StrafeMode(this, config));

	public Speed() {
		super("speed", "Speed", Category.MOVEMENT, "Strafe");
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	public boolean isPaused() {
		if (mc.player == null) {
			return true;
		}

		if (config.getPauseOnElytra().get() && mc.player.isGliding()) {
			return true;
		}

		if (config.getPauseInLiquid().get() && (mc.player.isTouchingWater() || mc.player.isInFluid())) {
			return true;
		}

		return PlayerUtils.shouldPause(config.getPauseOnEat().get(), config.getPauseOnShift().get(), false);
	}

	public void resetStage() {
		//TODO: бляээ
		//currentMode.stage = 0;
	}

	public enum Mode {
		STRAFE,
		VANILLA
	}
}
