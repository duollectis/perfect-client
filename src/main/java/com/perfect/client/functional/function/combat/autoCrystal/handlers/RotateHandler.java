package com.perfect.client.functional.function.combat.autoCrystal.handlers;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import com.perfect.client.functional.function.combat.autoCrystal.data.BreakInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.PlaceInfo;
import com.perfect.client.functional.function.combat.autoCrystal.helpers.RotationHelper;

public class RotateHandler extends FunctionHandler<AutoCrystal, AutoCrystalConfig> {

	public RotateHandler(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	@Override
	public void onAutoCrystalRotate() {
		function.autoCrystalTick();
		updateRotations();
	}

	@Override
	public void onAutoCrystalPostSendMovement() {
		if (!function.isStrictTiming()) {
			function.sendActions();
		}
	}

	private void updateRotations() {
		RotationHelper rotationHelper = function.getRotationHelper();
		PlaceInfo placeInfo = function.getPlaceInfo();
		BreakInfo breakInfo = function.getBreakInfo();

		rotationHelper.updateRotation(placeInfo, breakInfo);
	}
}
