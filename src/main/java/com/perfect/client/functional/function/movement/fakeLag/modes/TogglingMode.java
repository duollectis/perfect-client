package com.perfect.client.functional.function.movement.fakeLag.modes;

import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.movement.fakeLag.FakeLag;
import com.perfect.client.functional.function.movement.fakeLag.FakeLagConfig;
import com.perfect.client.functional.function.movement.fakeLag.utils.FakeLagMode;


public class TogglingMode extends FakeLagMode {

	private final TimerUtils toggleTimer = new TimerUtils();

	public TogglingMode(
			FakeLag function,
			FakeLagConfig config
	) {
		super(function, config);
	}

	@Override
	public void reset() {
		toggleTimer.reset();
	}

	@Override
	public void onTick() {
		if (toggleTimer.passedMillis(config.getLagTime().get())) {
			function.endFakeLag();
			function.startFakeLag();
		}
	}
}
