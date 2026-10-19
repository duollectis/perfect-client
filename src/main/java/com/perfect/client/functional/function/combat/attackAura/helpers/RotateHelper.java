package com.perfect.client.functional.function.combat.attackAura.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.RaycastUtils;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.SecureRandomUtils;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;


public class RotateHelper extends FunctionHelper<AttackAura, AttackAuraConfig> {

	public RotateHelper(AttackAura function, AttackAuraConfig config) {
		super(function, config);
	}

	
	public boolean isRotated(Entity entity) {
		if (mc.player == null) {
			return false;
		}

		Vec3d from = mc.player.getCameraPosVec(1.0f);

		Vec3d rotationVector = mc.player.getRotationVector(
				Managers.ROTATION.getRotatePitch(),
				Managers.ROTATION.getRotateYaw()
		);

		Vec3d to = RaycastUtils.getVec3dFromVector(from, rotationVector, config.getAttackRange().get());

		if (config.getRaytraceEntities().get()) {
			EntityHitResult entityHitResult = function.getTargetHelper().raytraceEntityResult(from, to);

			if (entityHitResult == null) {
				return false;
			}
			if (!entity.equals(entityHitResult.getEntity())) {
				return false;
			}
		}

		Optional<Vec3d> hitResult = RaycastUtils.raycast(entity.getBoundingBox(), from, to);

		if (hitResult.isEmpty()) {
			return false;
		}

		if (config.getRaytraceBlocks().get()) {
			return !function.getTargetHelper().raytraceBlock(from, hitResult.get());
		}

		return true;
	}

	
	public Vec3d getRotationExtrapolation(Entity entity, int ticksToAttack) {
		Vec3d extrapolated = Managers.PREDICT.extrapolate(entity, ticksToAttack + 2).getPos();

		double yDelta = Math.clamp(extrapolated.getY() - entity.getY(), -1.4, 1.4);

		return new Vec3d(
				entity.getX(),
				entity.getY() + yDelta,
				entity.getZ()
		);
	}

	
	public Angle transformRotation(float yaw, float pitch, float cooldownProgress) {
		float targetYawDelta = yaw - Managers.ROTATION.getSentYaw();
		float targetPitchDelta = pitch - Managers.ROTATION.getSentPitch();

		targetYawDelta = Math.clamp(targetYawDelta, -100.0f, 100.0f);
		targetPitchDelta = Math.clamp(targetPitchDelta, -100.0f, 100.0f);

		float speed = getRotationSpeed(
				Math.abs((targetYawDelta / 100.0f) * (targetPitchDelta / 100.0f)),
				cooldownProgress
		);

		float transformedYawDelta = targetYawDelta * speed;
		float transformedPitchDelta = targetPitchDelta * speed;

		Angle smoothed = smoothRotationDelta(transformedYawDelta, transformedPitchDelta);

		float resultYawDelta = MathUtils.absMin(smoothed.getYaw(), targetYawDelta);
		float resultPitchDelta = MathUtils.absMin(smoothed.getPitch(), targetPitchDelta);

		if (resultYawDelta != 0.0f || resultPitchDelta != 0.0f) {
			resultYawDelta += randomAngle();
			resultPitchDelta += randomAngle();
		}

		return deltaToRotation(resultYawDelta, resultPitchDelta);
	}

	
	private float randomAngle() {
		double sensitivity = Managers.SENSITIVITY.getSensitivityGcd();
		return (float) (sensitivity * SecureRandomUtils.random(1, 5) * SecureRandomUtils.randomSign());
	}

	
	private Angle deltaToRotation(float yawDelta, float pitchDelta) {
		float yaw = Managers.ROTATION.getSentYaw() + yawDelta;
		float pitch = Math.clamp(Managers.ROTATION.getSentPitch() + pitchDelta, -90.0f, 90.0f);

		return new Angle(yaw, pitch);
	}

	
	public Angle smoothRotationDelta(float targetYawDelta, float targetPitchDelta) {
		float alpha = config.getLegitRotationAlpha().getTo().floatValue();

		float yaw = alpha * targetYawDelta + (1.0f - alpha) * Managers.ROTATION.getSentYawDelta();
		float pitch = alpha * targetPitchDelta + (1.0f - alpha) * Managers.ROTATION.getSentPitch();

		return new Angle(yaw, pitch);
	}

	
	public float getRotationSpeed(float distance, float cooldownProgress) {
		float speed = (cooldownProgress + MathUtils.random(0.1f, 0.18f)) * distance;

		float baseSpeed = config.getLegitRotationSpeed().getFloat();

		return Math.clamp(speed, SecureRandomUtils.random(0.05f, 0.2f), 1.0f) * baseSpeed;
	}
}
