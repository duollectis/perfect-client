package com.perfect.client.core.utils.functions.interact;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.manager.Managers;

import static com.perfect.client.PerfectClient.mc;

public class Rotation extends EventListener {

	public float rotationYaw, rotationPitch;
	public Runnable runnable;
	private boolean rotated;

	public Rotation() {
		super();

		rotationYaw = Float.NaN;
		rotationPitch = Float.NaN;
	}

	@Override
	public void onRotate() {
		if (Float.isNaN(rotationYaw) || Float.isNaN(rotationPitch)) {
			return;
		}

		if (mc.player == null) {
			clear();
			return;
		}

		Managers.ROTATION.setRotateYaw(rotationYaw);
		Managers.ROTATION.setRotatePitch(rotationPitch);
		rotated = true;
	}

	@Override
	public void onHandleInput() {
		if (runnable == null || !rotated) {
			return;
		}

		runnable.run();
		clear();
	}

	public void rotate(float yaw, float pitch, Runnable runnable) {
		this.runnable = runnable;
		rotationYaw = yaw;
		rotationPitch = pitch;
	}

	public void clear() {
		runnable = null;
		rotationYaw = Float.NaN;
		rotationPitch = Float.NaN;
		PerfectClient.EVENT_MANAGER.unsubscribe(this);
	}
}
