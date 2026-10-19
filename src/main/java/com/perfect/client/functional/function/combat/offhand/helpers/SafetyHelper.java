package com.perfect.client.functional.function.combat.offhand.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.combat.offhand.Offhand;
import com.perfect.client.functional.function.combat.offhand.OffhandConfig;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;


public class SafetyHelper extends FunctionHelper<Offhand, OffhandConfig> {

	public SafetyHelper(Offhand offhand, OffhandConfig config) {
		super(offhand, config);
	}

	public boolean isSafe() {
		if (mc.player == null || mc.world == null) {
			return false;
		}

		if (!function.popTimer.passedMillis(getSafePopTime())) {
			return false;
		}
		if (mc.player.isGliding()) {
			return false;
		}
		float health = getHealth();

		if (!config.getSmart().get()) {
			return health > config.getMinHealth().get();
		}
		if (health <= config.getMinHealth().get()) {
			return false;
		}

		if (mc.player.fallDistance > 3 && health - mc.player.fallDistance * 0.5 <= config.getMinHealth().get()) {
			return false;
		}

		float maxDamage = 0.0f;

		for (Entity entity : getEntities()) {
			float damage = getDamage(entity.getPos(), null);
			if (damage > maxDamage) {
				maxDamage = damage;
			}
		}

		for (BlockPos pos : PositionMath.getCube(20)) {
			Block block = mc.world.getBlockState(pos).getBlock();
			if (block == Blocks.RESPAWN_ANCHOR || block instanceof BedBlock) {
				float damage = getDamage(pos.toCenterPos(), pos);
				if (damage > maxDamage) {
					maxDamage = damage;
				}
			}
		}

		return health - maxDamage > config.getMinHealth().get();
	}

	private List<Entity> getEntities() {
		if (mc.world == null || mc.player == null) {
			return new ArrayList<>();
		}

		List<Entity> entities = WorldUtils.getEntities(
				mc.player.getBoundingBox().expand(20),
				function::isDangerousEntity
		);

		entities.addAll(function.lastCrystals.keySet());

		return entities;
	}

	private int getSafePopTime() {
		if (mc.player == null) {
			return 0;
		}
		if (HoleUtils.isInHole(mc.player)) {
			return 300;
		}
		else {
			return 1000;
		}
	}

	private float getDamage(Vec3d position, BlockPos ignore) {
		if (mc.player == null) {
			return 0;
		}
		return DamageCalculator.getExplosionDamage(
				mc.player,
				mc.player.getBoundingBox(),
				position,
				false,
				false,
				true,
				6,
				ignore
		);
	}

	private float getHealth() {
		if (mc.player == null) {
			return 0;
		}

		float health = mc.player.getHealth();
		if (mc.player.getStatusEffect(StatusEffects.ABSORPTION) != null) {
			health += mc.player.getAbsorptionAmount();
		}

		return health;
	}
}
