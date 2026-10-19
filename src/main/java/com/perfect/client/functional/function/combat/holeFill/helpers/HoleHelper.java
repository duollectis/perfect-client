package com.perfect.client.functional.function.combat.holeFill.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.hole.Hole;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.holeFill.HoleFill;
import com.perfect.client.functional.function.combat.holeFill.HoleFillConfig;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;


public class HoleHelper extends FunctionHelper<HoleFill, HoleFillConfig> {

	public HoleHelper(HoleFill function, HoleFillConfig config) {
		super(function, config);
	}

	public Hole getHole(BlockPos pos) {
		return HoleUtils.getHole(
				pos,
				config.getSingleHoles().get(),
				config.getDoubleHoles().get(),
				config.getQuadHoles().get(),
				config.getHeight().get(),
				true
		);
	}

	public boolean isGoodHole(Hole hole, BlockPos pos) {
		if (hole == null) {
			return false;
		}
		if (holeHasEntity(hole)) {
			return false;
		}
		if (isUnreachable(pos)) {
			return false;
		}
		if (isUnsafePos(pos)) {
			return false;
		}

		if (config.getWithoutTarget().get()) {
			return true;
		}
		else {
			return function.getTargetHelper().holeHasTargetNear(pos);
		}
	}

	private boolean isUnsafePos(BlockPos pos) {
		if (mc.player == null) {
			return true;
		}
		if (config.getWithoutTarget().get()) {
			return false;
		}

		boolean inHole = HoleUtils.isInHole(mc.player);

		if (config.getOnlyInHole().get() && !inHole) {
			return true;
		}
		if (config.getIgnoreRangeInHole().get() && inHole) {
			return false;
		}

		return !PositionMath.isOutOfRange(PositionMath.closestVec3d(mc.player, pos), config.getMinRange().get());
	}

	private boolean isUnreachable(BlockPos pos) {
		BlockHitResult result = HitResultUtils.getPlaceResult(pos);
		if (result == null) {
			return true;
		}
		return PositionMath.isOutOfRange(result.getPos(), config.getRange().get());
	}

	private boolean holeHasEntity(Hole hole) {
		for (BlockPos holePos : hole.positions()) {
			if (BlockUtils.hasEntity(holePos, entity -> entity.intersectionChecked)) {
				return true;
			}
		}
		return false;
	}
}
