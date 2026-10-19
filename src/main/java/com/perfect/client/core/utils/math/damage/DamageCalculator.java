package com.perfect.client.core.utils.math.damage;

import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

import static com.perfect.client.PerfectClient.mc;


public class DamageCalculator {

	private static final ExposureCalculator exposureCalculator = new ExposureCalculator();

	public static float getExplosionDamage(
			LivingEntity target,
			Box targetBox,
			Vec3d source,
			boolean ignoreTerrain,
			boolean placing,
			boolean withExposure,
			int explosionPower,
			BlockPos ignore
	) {
		return getExplosionDamage(
				target,
				targetBox,
				source,
				ignoreTerrain,
				placing,
				withExposure,
				explosionPower,
				ignore,
				null
		);
	}

	public static float getExplosionDamage(
			LivingEntity entity,
			Box box,
			Vec3d source,
			boolean ignoreTerrain,
			boolean placing,
			boolean withExposure,
			int explosionPower,
			BlockPos ignored,
			List<BlockPos> support
	) {
		if (mc.world == null) {
			return 0f;
		}

		if (EntityUtils.isCreative(entity)) {
			return 0f;
		}

		int explosionRadius = explosionPower * 2;

		if (PositionMath.isOutOfRange(box.getHorizontalCenter(), explosionRadius)) {
			return 0f;
		}

		float impact = getImpact(
				explosionRadius,
				withExposure,
				source,
				entity,
				box,
				ignoreTerrain,
				placing,
				ignored,
				support
		);

		float damage = getDamageByDistance(impact, explosionRadius);
		damage = DamageUtils.modifyDamageByDifficulty(damage);

		if (damage == 0f)
			return 0f;

		damage = DamageUtils.getDamageLeft(entity, damage);
		damage = DamageUtils.modifyDamageByResistance(entity, damage);
		damage = DamageUtils.modifyDamageByProtection(entity, damage);

		return Math.max(damage, 0f);
	}

	public static BlockHitResult raycast(
			RaycastContext context,
			boolean ignoreTerrain,
			boolean placing,
			BlockPos ignored,
			List<BlockPos> support
	) {
		RaycastCalculator calculator = exposureCalculator.getRaycastCalculator();
		return calculator.getRaycastResult(context, ignoreTerrain, placing, ignored, support);
	}

	private static float getDamageByDistance(float impact, int radius) {
		return ((impact * impact + impact) * radius * 3.5f + 1f);
	}

	private static float getImpact(
			int radius,
			boolean withExposure,
			Vec3d source,
			LivingEntity entity,
			Box box,
			boolean ignoreTerrain,
			boolean placing,
			BlockPos ignored,
			List<BlockPos> support
	) {
		float exposure;

		if (withExposure) {
			exposure = exposureCalculator.getExposure(source, entity, box, ignoreTerrain, placing, ignored, support);
		}
		else {
			exposure = 1f;
		}

		float distance = (float) Math.sqrt(entity.squaredDistanceTo(source));
		return (1f - distance / radius) * exposure;
	}
}
