package com.perfect.client.functional.function.player.rotation;

import com.perfect.client.core.event.events.movement.UpdateVelocityEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.rotation.Angle;


public class Rotation extends Function {

	private final RotationConfig config = new RotationConfig(this);

	private final Angle memoryAngle = new Angle();

	public Rotation() {
		super("rotation", "Rotation", Category.PLAYER, "No Rotate", "Rotate");
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			return;
		}

		memoryAngle.set(mc.player.getYaw(), mc.player.getPitch());
	}

	@Override
	public void onDisable() {
		memoryAngle.reset();
	}

	@Override
	public void onRotationFunctionRotate() {
		if (mc.player == null)
			return;

		Angle angle = getAngle();

		if (angle.isYawSet()) {
			Managers.ROTATION.setRotateYaw(angle.getYaw());
		}
		if (angle.isPitchSet()) {
			Managers.ROTATION.setRotatePitch(angle.getPitch());
		}
	}

	@Override
	public void onPreTick() {
		if (mc.player == null)
			return;

		if (memoryAngle.isYawSet()) {
			memoryAngle.setYaw(mc.player.getYaw());
		}
		if (memoryAngle.isPitchSet()) {
			memoryAngle.setPitch(mc.player.getPitch());
		}
	}

	@Override
	public void onPreUpdateVelocity(UpdateVelocityEvent event) {
		if (mc.player == null) {
			return;
		}

		Angle angle = getAngle();

		if (angle.isYawSet()) {
			Managers.ROTATION.setRotateYaw(angle.getYaw());
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.player == null || !config.getClientLook().get()) {
			return;
		}

		Angle angle = getAngle();

		if (angle.isYawSet()) {
			mc.player.setYaw(angle.getYaw());
			mc.player.lastYaw = angle.getYaw();
			mc.player.renderYaw = angle.getYaw();
			mc.player.lastRenderYaw = angle.getYaw();
		}
		if (angle.isPitchSet()) {
			mc.player.setPitch(angle.getPitch());
			mc.player.lastPitch = angle.getPitch();
			mc.player.renderPitch = angle.getPitch();
			mc.player.lastRenderPitch = angle.getPitch();
		}
	}

	private Angle getAngle() {
		float yaw = switch (config.getYawMode().get()) {
			case NONE -> Float.NaN;
			case SMART -> smartYaw();
			case CUSTOM -> config.getYawValue().get().floatValue();
			case MEMORY -> memoryAngle.getYaw();
		};

		float pitch = switch (config.getPitchMode().get()) {
			case NONE -> Float.NaN;
			case SMART -> smartPitch();
			case CUSTOM -> config.getPitchValue().get().floatValue();
			case MEMORY -> memoryAngle.getPitch();
		};

		return new Angle(yaw, pitch);
	}

	private float smartYaw() {
		if (mc.player == null)
			return 0;
		return Math.round((memoryAngle.getYaw() + 1f) / 45f) * 45f;
	}

	private float smartPitch() {
		if (mc.player == null)
			return 0;
		return Math.round((memoryAngle.getPitch() + 1f) / 30f) * 30f;
	}

	public enum Mode {
		NONE,
		CUSTOM,
		SMART,
		MEMORY
	}
}
