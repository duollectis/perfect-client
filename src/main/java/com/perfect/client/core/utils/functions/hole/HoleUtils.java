package com.perfect.client.core.utils.functions.hole;

import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.game.world.BlockUtils.isBlastResist;
import static com.perfect.client.core.utils.game.world.BlockUtils.isCollidable;


public class HoleUtils {

	public static Hole getHole(BlockPos pos, int depth) {
		return getHole(pos, depth, true);
	}

	public static Hole getHole(BlockPos pos, int depth, boolean floor) {
		return getHole(pos, true, true, true, depth, floor);
	}

	public static Hole getHole(BlockPos pos, boolean s, boolean d, boolean q, int depth, boolean floor) {
		if (!isHole(pos, depth, floor)) {
			return null;
		}

		if (!isHoleBlock(pos.west()) || !isHoleBlock(pos.north())) {
			return null;
		}

		boolean x = isHole(pos.east(), depth, floor) && isHoleBlock(pos.east().north()) && isHoleBlock(pos.east(2));
		boolean z = isHole(pos.south(), depth, floor) && isHoleBlock(pos.south().west()) && isHoleBlock(pos.south(2));

		// Single
		if (s && !x && !z && isHoleBlock(pos.east()) && isHoleBlock(pos.south())) {
			return new Hole(pos, HoleType.SINGLE);
		}

		// Quad
		if (q && x && z && isHole(pos.south().east(), depth, floor) && isHoleBlock(pos.east().east().south())
				&& isHoleBlock(pos.south().south().east())) {
			return new Hole(pos, HoleType.QUAD);
		}

		if (!d) {
			return null;
		}

		// DoubleX
		if (x && !z && isHoleBlock(pos.south()) && isHoleBlock(pos.south().east())) {
			return new Hole(pos, HoleType.DOUBLE_X);
		}

		// DoubleZ
		if (z && !x && isHoleBlock(pos.east()) && isHoleBlock(pos.south().east())) {
			return new Hole(pos, HoleType.DOUBLE_Z);
		}

		return null;
	}

	public static boolean isHoleBlock(BlockPos pos) {
		return isBlastResist(pos) && isFullBlock(pos);
	}

	private static boolean isHole(BlockPos pos, int depth, boolean floor) {
		if (floor && !isBlastResist(pos.down())) {
			return false;
		}

		if (isCollidable(pos) && isFullBlock(pos)) {
			return false;
		}

		for (int i = 1; i < depth; i++) {
			if (isCollidable(pos.up(i))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isFullBlock(BlockPos pos) {
		VoxelShape shape = BlockUtils.getState(pos).getCollisionShape(mc.world, pos);
		if (shape.isEmpty()) {
			return false;
		}
		return shape.getBoundingBox().getLengthY() >= 0.4d;
	}


	public static boolean isInHole(LivingEntity player) {
		return isInHole(player.getBlockPos());
	}

	public static boolean isInHole(BlockPos pos) {
		if (isHole(pos, HoleType.SINGLE)) {
			return true;
		}

		// DoubleX
		if (isHole(pos, HoleType.DOUBLE_X)
				|| isHole(pos.add(-1, 0, 0), HoleType.DOUBLE_X)) {
			return true;
		}

		// DoubleZ
		if (isHole(pos, HoleType.DOUBLE_Z)
				|| isHole(pos.add(0, 0, -1), HoleType.DOUBLE_Z)) {
			return true;
		}

		// Quad
		return isHole(pos, HoleType.QUAD)
				|| isHole(pos.add(-1, 0, -1), HoleType.QUAD)
				|| isHole(pos.add(-1, 0, 0), HoleType.QUAD)
				|| isHole(pos.add(0, 0, -1), HoleType.QUAD);
	}

	private static boolean isHole(BlockPos pos, HoleType type) {
		Hole hole = getHole(pos, 1);
		return hole != null && hole.type == type;
	}

	public static boolean isBedrock(BlockPos[] positions) {
		for (BlockPos pos : positions) {
			for (Direction direction : Direction.values()) {
				if (direction == Direction.UP) {
					continue;
				}
				if (!isBedrockBlock(pos.offset(direction))) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isBedrockBlock(BlockPos pos) {
		return BlockUtils.isOf(pos, Blocks.BEDROCK) || !isHoleBlock(pos);
	}
}
