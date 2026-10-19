package com.perfect.client.core.utils.game.rotation;

import com.perfect.client.core.manager.Managers;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.PerfectClient.mc;

public class RotationUtils {

	
	public static float getYawRange(Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}
		return Math.abs(RotationUtils.getYaw(pos) - mc.player.getYaw());
	}

	
	public static float angleStep(float current, float target, float step) {
		float delta = target - current;

		if (delta > 0) {
			delta = Math.min(delta, step);
		}
		if (delta < 0) {
			delta = Math.max(delta, -step);
		}

		return current + delta;
	}

	
	public static float getYaw(Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}
		return getYaw(mc.player.getPos(), pos);
	}

	
	public static float getYaw(Vec3d playerPos, Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}
		return Managers.ROTATION.getSentYaw() + MathHelper.wrapDegrees(getYawDegrees(playerPos, pos));
	}

	
	private static float getYawDegrees(Vec3d playerPos, Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}

		double degrees = Math.toDegrees(Math.atan2(pos.getZ() - playerPos.getZ(), pos.getX() - playerPos.getX()));
		return (float) degrees - 90.0f - Managers.ROTATION.getSentYaw();
	}

	
	public static float getPitch(Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}
		return getPitch(mc.player.getPos(), pos);
	}

	
	public static float getPitch(Vec3d playerPos, Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}

		double diffX = pos.getX() - playerPos.getX();
		double diffY = pos.getY() - (playerPos.getY() + mc.player.getEyeHeight(mc.player.getPose()));
		double diffZ = pos.getZ() - playerPos.getZ();

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		return Managers.ROTATION.getSentPitch() + MathHelper.wrapDegrees(getPitchDegrees(diffXZ, diffY));
	}

	
	private static float getPitchDegrees(double diffXZ, double diffY) {
		if (mc.player == null) {
			return 0.0f;
		}

		return (float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - Managers.ROTATION.getSentPitch();
	}
}
