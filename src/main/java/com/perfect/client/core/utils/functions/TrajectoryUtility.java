package com.perfect.client.core.utils.functions;

import com.perfect.client.core.utils.game.entity.PlayerUtils;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.PerfectClient.mc;

public class TrajectoryUtility {

	public static float calculateArrowPitch(Vec3d targetPos, float charge) {
		if (mc.player == null) {
			return Float.NaN;
		}

		double GRAVITY = 0.05000000074505806;

		Vec3d playerVel = PlayerUtils.getVelocity();
		Vec3d toTarget = targetPos.subtract(mc.player.getEyePos());

		double horizontalDist = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);

		if (horizontalDist < 0.001) {
			return (float) (Math.PI / 4.0);
		}

		double dirX = toTarget.x / horizontalDist;
		double dirZ = toTarget.z / horizontalDist;

		double playerVelProjection = playerVel.x * dirX + playerVel.z * dirZ;
		double effectiveHorizontalVel = 2.8 * charge + playerVelProjection;

		if (effectiveHorizontalVel <= 0.01) {
			return Float.NaN;
		}

		double estimatedFlightTime = horizontalDist / effectiveHorizontalVel;

		double correctedVerticalPos = targetPos.y - (playerVel.y * estimatedFlightTime);

		double verticalDiff = correctedVerticalPos - mc.player.getEyePos().y;

		double horizontal = horizontalDist * horizontalDist;
		double vertical = effectiveHorizontalVel * effectiveHorizontalVel;

		double A = (GRAVITY * horizontal) / (2.0 * vertical);
		double B = -horizontalDist;
		double C = A + verticalDiff;

		double D = B * B - 4.0 * A * C;

		if (D < 0.0) {
			return Float.NaN;
		}

		double sqrtD = Math.sqrt(D);
		double tan1 = (-B + sqrtD) / (2.0 * A);
		double tan2 = (-B - sqrtD) / (2.0 * A);

		double angle1 = Math.atan(tan1);
		double angle2 = Math.atan(tan2);

		return (float) Math.min(angle1, angle2);
	}
}
