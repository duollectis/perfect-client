package com.perfect.client.core.utils.functions.target;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.manager.system.FunctionManager.antiBot;
import static com.perfect.client.manager.system.FunctionManager.suicide;

public class TargetUtils {

	public static List<LivingEntity> getTargetsInRange(double enemyRange, boolean selfAttack) {
		return getTargetsInRange(enemyRange, selfAttack, Objects::nonNull);
	}

	public static List<LivingEntity> getTargetsInRange(
			double enemyRange,
			boolean selfAttack,
			Predicate<LivingEntity> predicate
	) {
		if (mc.world == null || mc.player == null) {
			return new ArrayList<>();
		}
		return getTargetsInRange(
				PositionMath.boxFromCenter(enemyRange + 1, mc.player.getEyePos()),
				enemyRange,
				selfAttack,
				predicate
		);
	}

	public static List<LivingEntity> getTargetsInRange(
			Box box,
			double enemyRange,
			boolean selfAttack,
			Predicate<LivingEntity> predicate
	) {
		if (mc.world == null || mc.player == null) {
			return new ArrayList<>();
		}

		ArrayList<LivingEntity> entities = new ArrayList<>();

		for (Entity entity : WorldUtils.getEntities(box)) {
			if (!isTargetEntity(entity, enemyRange, selfAttack, predicate)) {
				continue;
			}

			entities.add((LivingEntity) entity);
		}

		return entities;
	}

	public static boolean isTargetEntity(
			Entity entity,
			double enemyRange,
			boolean selfAttack,
			Predicate<LivingEntity> predicate
	) {
		if (mc.player == null) {
			return false;
		}

		return isTargetEntity(mc.player.getEyePos(), entity, enemyRange, selfAttack, predicate);
	}

	
	public static boolean isTargetEntity(
			Vec3d center,
			Entity entity,
			double enemyRange,
			boolean selfAttack,
			Predicate<LivingEntity> predicate
	) {
		if (mc.player == null) {
			return false;
		}
		if (isClientPlayer(entity, selfAttack)) {
			return false;
		}
		if (selfAttack && !suicide.isEnabled() && entity.equals(mc.player)) {
			return false;
		}
		if (!(entity instanceof LivingEntity living)) {
			return false;
		}
		if (!living.isAlive()) {
			return false;
		}
		if (Managers.FRIEND.isFriend(living) && entity != mc.player) {
			return false;
		}
		if (antiBot.isBot(entity)) {
			return false;
		}
		if (predicate != null && !predicate.test(living)) {
			return false;
		}
		return !PositionMath.isOutOfRange(
				PositionMath.closestVec3d(center, living.getBoundingBox()),
				enemyRange
		);
	}

	private static boolean isClientPlayer(Entity entity, boolean selfAttack) {
		if (selfAttack && suicide.isEnabled()) {
			return false;
		}
		return entity.equals(mc.player) || entity.getName().equals(mc.player.getName());
	}

	public static LivingEntity getTarget(
			double range,
			TargetPriority priority,
			boolean selfAttack,
			Predicate<LivingEntity> predicate
	) {
		return getTarget(getTargetsInRange(range, selfAttack, predicate), priority);
	}

	
	public static LivingEntity getTarget(List<LivingEntity> entities, TargetPriority priority) {
		if (entities.isEmpty()) {
			return null;
		}

		return entities.stream().sorted(Comparator.comparing(
				entity -> getCompareValue(entity, priority)
		)).toList().getFirst();
	}

	
	public static double getCompareValue(LivingEntity entity, TargetPriority priority) {
		return switch (priority) {
			case HEALTH -> entity.getHealth() + entity.getAbsorptionAmount();
			case RANGE -> PlayerUtils.squaredDistanceFromEyes(entity.getPos());
			case FIELD_OF_VIEW -> getFov(entity);
		};
	}

	
	private static float getFov(LivingEntity entity) {
		if (mc.player == null) {
			return 0;
		}

		Vec3d pos = entity.getBoundingBox().getCenter();

		float yaw = RotationUtils.getYaw(pos);
		float pitch = RotationUtils.getPitch(pos);

		float yawDiff = Math.abs(yaw - mc.player.getYaw());
		float pitchDiff = Math.abs(pitch - mc.player.getPitch());

		return yawDiff * yawDiff + pitchDiff * pitchDiff;
	}
}
