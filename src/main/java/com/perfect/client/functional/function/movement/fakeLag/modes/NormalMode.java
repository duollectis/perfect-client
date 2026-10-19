package com.perfect.client.functional.function.movement.fakeLag.modes;

import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.movement.fakeLag.FakeLag;
import com.perfect.client.functional.function.movement.fakeLag.FakeLagConfig;
import com.perfect.client.functional.function.movement.fakeLag.utils.FakeLagMode;


public class NormalMode extends FakeLagMode {

	private final TimerUtils disableTimer = new TimerUtils();

	public NormalMode(
			FakeLag function,
			FakeLagConfig config
	) {
		super(function, config);
	}

	@Override
	public void reset() {
		disableTimer.reset();
	}

	@Override
	public void onTick() {
		if (config.getAutoDisable().get() && disableTimer.passedMillis(config.getTimeToDisable().get())) {
			function.disable();
		}
	}
}
