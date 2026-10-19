package com.perfect.client.functional.function.movement.step.modes;

import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.functional.function.movement.step.Step;
import com.perfect.client.functional.function.movement.step.StepConfig;
import com.perfect.client.functional.function.movement.step.utils.StepMode;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;


public class SlowMode extends StepMode {

	private static final double MIN_STEP_HEIGHT = 0.001;

	private boolean isInStep;
	private double stepHeight;

	public SlowMode(
			Step function,
			StepConfig config
	) {
		super(function, config);
	}

	@Override
	public void onDisable() {
		PlayerUtils.setStep(0.6);
	}

	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		if (mc.player == null) {
			return;
		}

		PlayerUtils.setStep(config.getNormalHeight().get());

		if (!needNormalStep(event) || !canStep(true)) {
			PlayerUtils.setStep(0.6);

			doSlowStep(event);
		}
	}

	
	private void doSlowStep(PlayerMoveEvent event) {
		assert mc.player != null;

		if (needSlowStep(event)) {
			Vec3d stepMovement = getStepMovement(event);

			if (stepMovement.getY() < MIN_STEP_HEIGHT) {
				return;
			}

			if (mc.player.isOnGround()) {
				stepHeight = stepMovement.getY();
			}

			double yMovement = Math.min(getSlowSpeed(), stepMovement.getY());

			event.setY(yMovement);

			Vec3d vel = mc.player.getVelocity();
			mc.player.setVelocity(vel.x, config.getJumpSpeed().fromBPS(), vel.z);

			isInStep = true;
		}
		else {
			isInStep = false;
		}
	}

	private double getSlowSpeed() {
		double speed = config.getSpeed().fromBPS();
		return speed + ((stepHeight - 1) * config.getHeightSpeedFactor().get());
	}

	private boolean needNormalStep(PlayerMoveEvent event) {
		assert mc.player != null;

		Vec3d stepMovement = mc.player.adjustMovementForCollisions(event.getMovement());
		return stepMovement.getY() >= MIN_STEP_HEIGHT;
	}

	
	private Vec3d getStepMovement(PlayerMoveEvent event) {
		assert mc.player != null;

		boolean onGround = mc.player.isOnGround();

		mc.player.setOnGround(true);
		PlayerUtils.setStep(config.getSlowHeight().get());

		Vec3d stepMovement = mc.player.adjustMovementForCollisions(new Vec3d(event.getX(), -1000, event.getZ()));

		PlayerUtils.setStep(0.6);
		mc.player.setOnGround(onGround);

		return stepMovement;
	}

	private boolean needSlowStep(PlayerMoveEvent event) {
		assert mc.player != null;

		if (event.getType() != MovementType.SELF) {
			return false;
		}
		if (!canStep(false)) {
			return false;
		}
		if (config.getOnlyOnGround().get() && !isOnGround()) {
			return false;
		}

		return mc.player.horizontalCollision;
	}

	private boolean isOnGround() {
		assert mc.player != null;

		if (isInStep) {
			return mc.player.getY() >= mc.player.lastY;
		}

		return mc.player.isOnGround();
	}
}
