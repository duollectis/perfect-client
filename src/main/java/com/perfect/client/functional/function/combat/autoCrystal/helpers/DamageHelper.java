package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;


public class DamageHelper extends FunctionHelper<AutoCrystal, AutoCrystalConfig> {

	public DamageHelper(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	public float getMinDamageForTarget(LivingEntity target) {
		float health = (float) EntityUtils.getTotalHealth(target);
		return Math.min(health, config.getMinDamage().getFloat());
	}

	public float getSelfDamage(Vec3d crystalPos, boolean placing, boolean exposure) {
		if (mc.player == null) {
			return 0f;
		}
		return getDamageIternal(mc.player, mc.player.getBoundingBox(), crystalPos, false, placing, exposure);
	}

	public float getDamage(LivingEntity entity, Box entityBox, Vec3d crystalPos, boolean placing, boolean exposure) {
		return getDamageIternal(entity, entityBox, crystalPos, config.getIgnoreTerrain().get(), placing, exposure);
	}

	private float getDamageIternal(
			LivingEntity entity,
			Box entityBox,
			Vec3d crystalPos,
			boolean ignoreTerrain,
			boolean placing,
			boolean exposure
	) {
		return DamageCalculator.getExplosionDamage(
				entity,
				entityBox,
				crystalPos,
				ignoreTerrain,
				placing,
				exposure,
				6,
				null
		);
	}
}
