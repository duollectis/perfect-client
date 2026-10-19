package com.perfect.client.functional.function.misc.autoWither.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.misc.autoWither.AutoWither;
import com.perfect.client.functional.function.misc.autoWither.AutoWitherConfig;
import com.perfect.client.functional.function.misc.autoWither.data.Block;
import com.perfect.client.functional.function.misc.autoWither.data.PlaceInfo;
import com.perfect.client.functional.function.misc.autoWither.data.WitherInfo;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PositionHelper extends FunctionHelper<AutoWither, AutoWitherConfig> {

	public PositionHelper(AutoWither function, AutoWitherConfig config) {
		super(function, config);
	}

	public WitherInfo getWitherInfo() {
		for (BlockPos pos : getCube()) {
			if (isOutOfRange(pos)) {
				continue;
			}

			WitherInfo witherInfo = getWitherInfoAt(pos);

			if (witherInfo == null) {
				continue;
			}

			if (!fillHitResults(witherInfo)) {
				continue;
			}

			return witherInfo;
		}
		return null;
	}

	private boolean fillHitResults(WitherInfo info) {
		assert mc.player != null;

		sort(info);

		List<BlockPos> accepted = new ArrayList<>();

		for (PlaceInfo block : info.getBlocks()) {
			BlockPos pos = block.getPos();

			BlockHitResult hitResult = null;

			switch (block.getBlock()) {
				case AIR -> {
					continue;
				}
				case SOUL_SAND -> {
					hitResult = HitResultUtils.getPlaceResult(pos, accepted);
				}
				case SKULL -> {
					hitResult = HitResultUtils.getInteractResult(pos.down());
				}
			}

			if (hitResult == null) {
				return false;
			}

			if (isOutOfRange(hitResult.getPos())) {
				return false;
			}

			block.setHitResult(hitResult);
			accepted.add(pos);
		}

		return true;
	}

	private boolean isOutOfRange(BlockPos pos) {
		assert mc.player != null;

		Vec3d vec3d = PositionMath.closestVec3d(mc.player.getEyePos(), pos);
		return isOutOfRange(vec3d);
	}

	private boolean isOutOfRange(Vec3d vec3d) {
		return PositionMath.isOutOfRange(vec3d, config.getRange().get());
	}

	// Блоки сортируются от самого дальнего к самому ближнему.
	// Также песок душ получает больший приоритет.

	private void sort(WitherInfo info) {
		assert mc.player != null;

		info.getBlocks().sort(Comparator.comparing(witherInfo -> {

			Vec3d pos = witherInfo.getPos().toCenterPos();

			double score = -PositionMath.squareDistance(pos);

			if (witherInfo.getBlock() == Block.SOUL_SAND) {
				score -= 100;
			}

			return score;
		}));
	}

	private WitherInfo getWitherInfoAt(BlockPos pos) {
		WitherInfo witherInfo = getWitherShape(pos, false);

		if (canPlaceWither(witherInfo)) {
			return witherInfo;
		}

		witherInfo = getWitherShape(pos, true);

		if (canPlaceWither(witherInfo)) {
			return witherInfo;
		}

		return null;
	}

	private boolean canPlaceWither(WitherInfo info) {
		for (PlaceInfo placeInfo : info.getBlocks()) {
			BlockPos pos = placeInfo.getPos();

			switch (placeInfo.getBlock()) {
				case AIR -> {
					if (!BlockUtils.isAir(pos)) {
						return false;
					}
				}
				case SOUL_SAND -> {
					if (!BlockUtils.isReplaceable(pos) && !isSoulSand(pos)) {
						return false;
					}
				}
				case SKULL -> {
					if (!BlockUtils.isReplaceable(pos) && !BlockUtils.isOf(pos, Blocks.WITHER_SKELETON_SKULL)) {
						return false;
					}
				}
			}

			if (placeInfo.getBlock() != Block.AIR && hasEntity(pos)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasEntity(BlockPos pos) {
		return BlockUtils.hasEntity(pos, this::isObstructiveEntity);
	}

	private boolean isObstructiveEntity(Entity entity) {
		return entity.intersectionChecked;
	}

	private boolean isSoulSand(BlockPos pos) {
		return BlockUtils.isOf(pos, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
	}

	private List<BlockPos> getCube() {
		return PositionMath.getCube(config.getRange().getInt() + 1);
	}

	// Визер собирается из центрального блока.
	// Под боковыми блоками всегда должен быть воздух.

	private WitherInfo getWitherShape(BlockPos pos, boolean rotate) {
		WitherInfo info = new WitherInfo(pos, rotate);

		info.add(new PlaceInfo(pos, Block.SOUL_SAND));
		info.add(new PlaceInfo(pos.down(), Block.SOUL_SAND));
		info.add(new PlaceInfo(pos.up(), Block.SKULL));

		Direction axis = rotate ? Direction.NORTH : Direction.EAST;

		BlockPos left = pos.offset(axis);
		BlockPos right = pos.offset(axis.getOpposite());

		info.add(new PlaceInfo(left, Block.SOUL_SAND));
		info.add(new PlaceInfo(left.down(), Block.AIR));
		info.add(new PlaceInfo(left.up(), Block.SKULL));

		info.add(new PlaceInfo(right, Block.SOUL_SAND));
		info.add(new PlaceInfo(right.down(), Block.AIR));
		info.add(new PlaceInfo(right.up(), Block.SKULL));

		return info;
	}
}
