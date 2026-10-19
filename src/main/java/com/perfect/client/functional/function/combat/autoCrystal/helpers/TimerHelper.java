package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;


public class TimerHelper extends FunctionHelper<AutoCrystal, AutoCrystalConfig> {

	private final TimerUtils placeTimer = new TimerUtils();
	private final TimerUtils breakTimer = new TimerUtils();
	private final TimerUtils facePlaceTimer = new TimerUtils();

	public TimerHelper(AutoCrystal autoCrystal, AutoCrystalConfig config) {
		super(autoCrystal, config);
	}

	public boolean isBreakTimerPassed() {
		if (config.getBreakDelay().get() <= 0) {
			return true;
		}

		if (breakTimer.passedMillis(config.getBreakDelay().get())) {
			breakTimer.reset();
			return true;
		}

		return false;
	}

	public boolean isPlaceTimerPassed() {
		if (config.getPlaceDelay().get() <= 0) {
			return true;
		}

		if (placeTimer.passedMillis(config.getPlaceDelay().get())) {
			placeTimer.reset();
			return true;
		}

		return false;
	}

	public boolean isFacePlaceTimerPassed() {
		if (config.getFacePlaceDelay().get() <= 0) {
			return true;
		}

		if (facePlaceTimer.passedMillis(config.getFacePlaceDelay().get())) {
			facePlaceTimer.reset();
			return true;
		}

		return false;
	}
}