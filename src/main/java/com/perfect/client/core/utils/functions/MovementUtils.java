package com.perfect.client.core.utils.functions;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;

import static com.perfect.client.PerfectClient.mc;

public class MovementUtils {

	private static final double MIN_MOVEMENT = 1.0e-7;

	
	public static Vec3d movementCorrection(float yaw, Vec3d movementInput, float speed) {
		double d = movementInput.lengthSquared();

		if (d < MIN_MOVEMENT) {
			return Vec3d.ZERO;
		}

		Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);

		float f = MathHelper.sin(yaw * (float) (Math.PI / 180.0));
		float g = MathHelper.cos(yaw * (float) (Math.PI / 180.0));

		double fixX = vec3d.x * g - vec3d.z * f;
		double fixZ = vec3d.z * g + vec3d.x * f;

		return new Vec3d(fixX, vec3d.y, fixZ);
	}

	public static float getMovementForward() {
		if (mc.player == null) {
			return 0f;
		}
		return Math.signum(mc.player.input.getMovementInput().y);
	}

	public static float getMovementSideways() {
		if (mc.player == null) {
			return 0f;
		}
		return Math.signum(mc.player.input.getMovementInput().x);
	}

	
	public static Vector2d forwardStrafe(double speed) {
		if (mc.player == null) {
			return null;
		}


		float forward = getMovementForward();
		float side = getMovementSideways();

		float yaw = mc.player.getYaw();

		if (forward == 0.0f && side == 0.0f) {
			return new Vector2d(0.0f, 0.0f);
		}

		else {
			if (forward != 0.0f) {
				if (side >= 1.0f) {
					yaw += (float) (forward > 0.0f ? -45 : 45);
					side = 0.0f;
				}
				else if (side <= -1.0f) {
					yaw += (float) (forward > 0.0f ? 45 : -45);
					side = 0.0f;
				}

				if (forward > 0.0f) {
					forward = 1.0f;
				}
				else if (forward < 0.0f) {
					forward = -1.0f;
				}
			}

			double mx = Math.cos(Math.toRadians(yaw + 90.0f));
			double mz = Math.sin(Math.toRadians(yaw + 90.0f));
			double velX = (double) forward * speed * mx + (double) side * speed * mz;
			double velZ = (double) forward * speed * mz - (double) side * speed * mx;
			return new Vector2d(velX, velZ);
		}
	}

	public static Vector2d forwardWithoutStrafe(double speed) {
		if (mc.player == null) {
			return new Vector2d(0, 0);
		}

		float yaw = mc.player.getYaw();
		final double x = speed * Math.cos(Math.toRadians(yaw + 90.0f));
		final double y = speed * Math.sin(Math.toRadians(yaw + 90.0f));
		return new Vector2d(x, y);
	}
}
