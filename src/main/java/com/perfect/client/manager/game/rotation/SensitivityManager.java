package com.perfect.client.manager.game.rotation;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.manager.Managers;
import lombok.Getter;

public class SensitivityManager extends Manager {

	private double prevSensitivity = -1;

	@Getter
	private double sensitivityGcd;

	
	@Override
	public void onPreTick() {
		double sensitivity = mc.options.getMouseSensitivity().getValue();

		if (prevSensitivity != sensitivity) {
			sensitivityGcd = Math.pow(mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2, 3.0) * 1.2;
			prevSensitivity = sensitivity;
		}
	}

	public float gcdYaw(float targetYaw) {
		if (mc.player == null) {
			return 0.0f;
		}
		return gcdAngle(targetYaw, Managers.ROTATION.getSentYaw());
	}

	public float gcdPitch(float targetPitch) {
		if (mc.player == null) {
			return 0.0f;
		}
		return gcdAngle(targetPitch, Managers.ROTATION.getSentPitch());
	}

	private float gcdAngle(float targetAngle, float currentAngle) {
		return (float) (targetAngle - (targetAngle - currentAngle) % sensitivityGcd);
	}
}
