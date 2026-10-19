package com.perfect.client.functional.function.combat.autoCrystal.handlers;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;

public class InputHandler extends FunctionHandler<AutoCrystal, AutoCrystalConfig> {

	public InputHandler(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	@Override
	public void onHandleInput() {
		if (function.isStrictTiming()) {
			function.sendActions();
		}
	}
}
