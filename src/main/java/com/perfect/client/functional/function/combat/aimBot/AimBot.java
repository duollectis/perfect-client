package com.perfect.client.functional.function.combat.aimBot;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.TrajectoryUtility;
import com.perfect.client.core.utils.functions.target.TargetPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AimBot extends Function {

	private final AimBotConfig config = new AimBotConfig(this);

	public AimBot() {
		super("aim-bot", "Aim Bot", Category.COMBAT, "Bot Aim Bot", "Bow Aim Assist");
	}

	private Angle preRotateAngle;

	@Override
	public void onRotate() {
		if (config.getRotate().is(Rotate.VANILLA)) {
			Angle angle = findAngle();

			if (angle != null) {
				Managers.ROTATION.setRotate(angle);
			}
		}
	}

	
	@Override
	public void onPreUsingItem() {
		if (mc.player == null) {
			return;
		}

		Angle angle = findAngle();

		if (angle != null) {
			preRotateAngle = new Angle(mc.player.getYaw(), mc.player.getPitch());

			mc.player.setYaw(angle.getYaw());
			mc.player.setPitch(angle.getPitch());

			if (config.getRotate().is(Rotate.INSTANT)) {
				Managers.ROTATION.packetRotate(angle);
			}
		}
	}

	@Override
	public void onPostUsingItem() {
		if (mc.player == null) {
			return;
		}

		if (preRotateAngle != null) {
			mc.player.setYaw(preRotateAngle.getYaw());
			mc.player.setPitch(preRotateAngle.getPitch());

			preRotateAngle = null;
		}
	}

	private Angle findAngle() {
		LivingEntity target = TargetUtils.getTarget(
				500.0,
				TargetPriority.RANGE,
				false,
				entity -> entity instanceof PlayerEntity
		);

		if (target == null) {
			return null;
		}

		return findAngle(target);
	}

	private Angle findAngle(LivingEntity target) {
		if (mc.player == null) {
			return null;
		}

		float charge = BowItem.getPullProgress(mc.player.getItemUseTime());

		for (int tick = 1; tick <= 300; tick++) {
			Box hitbox = Managers.PREDICT.extrapolate(target, tick + 1 + config.getExtrapolation().get()).getBox();

			for (Vec3d pos : getAimPositions(hitbox)) {
				float pitchResult = TrajectoryUtility.calculateArrowPitch(pos, charge);

				if (Float.isNaN(pitchResult)) {
					continue;
				}

				float yaw = RotationUtils.getYaw(pos);
				float pitch = (float) -Math.toDegrees(pitchResult);

				if (isHit(hitbox, tick, charge, yaw, pitch)) {
					return new Angle(yaw, pitch);
				}
			}
		}

		return null;
	}

	private List<Vec3d> getAimPositions(Box box) {
		List<Vec3d> list = new ArrayList<>();

		list.add(getAimPosition(box, 0.5, 0.75, 0.5));
		list.add(getAimPosition(box, 0.5, 0.0, 0.5));
		list.add(getAimPosition(box, 0.5, 1.0, 0.5));

		addSymmetryAimPosition(list, box, 1.0, 0.75, 0.5);

		return list;
	}

	private void addSymmetryAimPosition(List<Vec3d> list, Box box, double x, double y, double z) {
		list.add(getAimPosition(box, x, y, z));
		list.add(getAimPosition(box, 1.0 - x, y, 1.0 - z));
		list.add(getAimPosition(box, z, y, x));
		list.add(getAimPosition(box, 1.0 - z, y, 1.0 - x));
	}

	private Vec3d getAimPosition(Box box, double x, double y, double z) {
		return new Vec3d(
				box.minX + box.getLengthX() * x,
				box.minY + box.getLengthY() * y,
				box.minZ + box.getLengthZ() * z
		);
	}

	private boolean isHit(Box hitbox, int ticks, float charge, float yaw, float pitch) {
		if (mc.player == null) {
			return false;
		}

		double speed = charge * 2.8;

		double x = -Math.sin(yaw * MathHelper.RADIANS_PER_DEGREE) * Math.cos(pitch * MathHelper.RADIANS_PER_DEGREE);
		double y = -Math.sin(pitch * MathHelper.RADIANS_PER_DEGREE);
		double z = Math.cos(yaw * MathHelper.RADIANS_PER_DEGREE) * Math.cos(pitch * MathHelper.RADIANS_PER_DEGREE);

		Vec3d velocity = new Vec3d(x, y, z).normalize().multiply(speed).add(PlayerUtils.getVelocity());

		return isHit(hitbox, ticks, velocity);
	}

	private boolean isHit(Box hitbox, int ticks, Vec3d velocity) {
		if (mc.world == null || mc.player == null) {
			return false;
		}

		double motionX = velocity.x;
		double motionY = velocity.y;
		double motionZ = velocity.z;
		double x = mc.player.getEyePos().getX();
		double y = mc.player.getEyePos().getY();
		double z = mc.player.getEyePos().getZ();
		Vec3d lastPos;

		for (int i = 0; i < ticks; i++) {
			lastPos = new Vec3d(x, y, z);
			x += motionX;
			y += motionY;
			z += motionZ;

			if (y <= -65) {
				return false;
			}

			motionY -= 0.05000000074505806;

			Vec3d pos = new Vec3d(x, y, z);

			BlockHitResult blockHitResult = mc.world.raycast(new RaycastContext(
					lastPos,
					pos,
					RaycastContext.ShapeType.OUTLINE,
					RaycastContext.FluidHandling.NONE,
					mc.player
			));

			if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
				return false;
			}

			if (raycast(hitbox, lastPos, pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean raycast(
			Box hitbox,
			Vec3d min,
			Vec3d max
	) {
		if (mc.world == null) {
			return false;
		}

		double maxDistance = min.squaredDistanceTo(max);

		Optional<Vec3d> raycastPos = hitbox.raycast(min, max);

		if (hitbox.contains(min)) {
			return maxDistance >= 0.0;
		}
		else if (raycastPos.isPresent()) {
			Vec3d vec3d2 = raycastPos.get();
			return min.squaredDistanceTo(vec3d2) < maxDistance || maxDistance == 0.0;
		}

		return false;
	}

	public enum Rotate {
		INSTANT,
		VANILLA,
		LEGIT
	}
}
