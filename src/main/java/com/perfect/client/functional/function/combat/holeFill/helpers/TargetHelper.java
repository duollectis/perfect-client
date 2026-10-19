package com.perfect.client.functional.function.combat.holeFill.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.holeFill.HoleFill;
import com.perfect.client.functional.function.combat.holeFill.HoleFillConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class TargetHelper extends FunctionHelper<HoleFill, HoleFillConfig> {

	public TargetHelper(HoleFill function, HoleFillConfig config) {
		super(function, config);
	}

	public boolean holeHasTargetNear(BlockPos hole) {
		if (mc.world == null) {
			return false;
		}

		for (LivingEntity entity : getTargets()) {
			if (isGoodTarget(hole, entity)) {
				return true;
			}
		}

		return false;
	}

	private List<LivingEntity> getTargets() {
		return TargetUtils.getTargetsInRange(20, false);
	}

	private boolean isGoodTarget(BlockPos holePos, LivingEntity entity) {
		if (entity == null || !entity.isAlive() || entity == mc.player || !(entity instanceof PlayerEntity)) {
			return false;
		}
		if (HoleUtils.isInHole(entity)) {
			return false;
		}
		if (entity.getBlockY() < holePos.getY()) {
			return false;
		}

		Vec3d entityVec3d = getPredictedEntityPos(entity);
		Box entityBox = EntityUtils.getBoxAt(entity, entityVec3d);

		Vec3d closestHoleVec3d = PositionMath.closestVec3d(entityVec3d, holePos);
		Vec3d closestEntityVec3d = PositionMath.closestVec3d(closestHoleVec3d, entityBox);

		double distanceXZ = PositionMath.squareDistanceHorizontal(closestEntityVec3d, closestHoleVec3d);
		double distanceY = Math.abs(closestEntityVec3d.y - closestHoleVec3d.y);

		return distanceXZ <= MathUtils.square(config.getRadius().get()) && distanceY <= config.getYRange().get();
	}

	private Vec3d getPredictedEntityPos(LivingEntity entity) {
		return Managers.PREDICT.extrapolate(entity, config.getExtrapolation().get()).getPos();
	}
}