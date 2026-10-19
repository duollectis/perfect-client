package com.perfect.client.core.utils.functions.trap;

import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.game.world.BlockUtils.isBreakable;
import static com.perfect.client.core.utils.game.world.BlockUtils.isOf;

public class TrapUtils {

	private static final double ENEMY_DESYNC_OFFSET = 0.00000005;

	public static List<BlockPos> simpleSurround(BlockPos pos) {
		List<BlockPos> posList = new ArrayList<>();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			posList.add(pos.offset(direction));
		}

		return posList;
	}

	public static boolean isTrapped(LivingEntity entity, TrapType type) {
		return isTrapped(entity, type, true);
	}

	public static boolean isTrapped(LivingEntity entity, TrapType type, Predicate<BlockPos> predicate) {
		return isTrapped(getTrap(entity, type, true), predicate);
	}

	public static boolean isTrapped(LivingEntity entity, TrapType type, boolean placing) {
		return isTrapped(getTrap(entity, type, placing));
	}

	public static boolean isTrapped(List<BlockPos> trap) {
		return isTrapped(trap, BlockUtils::isBlastResist);
	}

	public static boolean isTrapped(List<BlockPos> trap, Predicate<BlockPos> predicate) {
		for (BlockPos pos : trap) {
			if (!predicate.test(pos)) {
				return false;
			}
		}
		return true;
	}

	@SafeVarargs
	public static List<BlockPos> getTrap(
			LivingEntity entity,
			TrapType type,
			Class<? extends Block>... replaceableBlocks
	) {
		return getTrap(entity, type, true, replaceableBlocks);
	}

	@SafeVarargs
	public static List<BlockPos> getTrap(
			LivingEntity entity,
			TrapType type,
			boolean placing,
			Class<? extends Block>... replaceableBlocks
	) {
		if (entity == null) {
			return new ArrayList<>();
		}
		BlockPos center = type.trapCenter(entity);
		return getTrapIternal(
				true,
				placing,
				center,
				type,
				entity,
				new ArrayList<>(),
				new ArrayList<>(),
				Arrays.asList(replaceableBlocks)
		);
	}

	
	private static List<BlockPos> getTrapIternal(
			boolean start,
			boolean placing,
			BlockPos center,
			TrapType type,
			LivingEntity target,
			List<BlockPos> result,
			List<BlockPos> intersected,
			List<Class<? extends Block>> replaceableBlocks
	) {
		if (result.contains(center)
				|| intersected.contains(center)
				|| (placing && BlockUtils.isOutOfBorder(center)))
			return result;

		boolean antiStep = type == TrapType.TRAP_WITH_ANTI_STEP;

		Box centerBox = new Box(center);

		List<Entity> entities = BlockUtils.getEntities(
				centerBox,
				entity -> entity.isAlive() && entity.intersectionChecked && !(entity instanceof EndCrystalEntity)
		);
		boolean intersects = false;

		if (isReplaceable(center, replaceableBlocks) || start) {
			for (Entity entity : entities) {
				if (!entity.equals(mc.player) && !intersectsWithoutDesync(entity, centerBox)) {
					continue;
				}
				if (entity.equals(target) && type.isFloor()) {
					BlockPos floor = center.up(type.getFloorHeight());

					if (!result.contains(floor)
							&& canPlaceBlock(center, antiStep, replaceableBlocks)
							&& surroundEntities(floor).isEmpty()) {
						result.add(floor);
					}

				}
				intersects = true;
			}
		}

		boolean needSurround = intersects && (isReplaceable(center, replaceableBlocks) || start);

		if (needSurround) {
			intersected.add(center);
			Direction.Type.HORIZONTAL.forEach(direction -> getTrapIternal(
					false,
					placing,
					center.offset(direction),
					type,
					target,
					result,
					intersected,
					replaceableBlocks
			));
		}

		else if (canPlaceBlock(center, antiStep, replaceableBlocks) && type.isSurrounding()) {
			result.add(center);
		}

		return result;
	}

	private static boolean intersectsWithoutDesync(Entity entity, Box box) {
		Box bb = entity.getBoundingBox().expand(-ENEMY_DESYNC_OFFSET, 0.0, -ENEMY_DESYNC_OFFSET);
		return bb.intersects(box);
	}

	
	private static boolean canPlaceBlock(
			BlockPos pos,
			boolean antiStep,
			List<Class<? extends Block>> replaceableBlocks
	) {
		return isReplaceable(pos, replaceableBlocks) || antiStep || isBreakable(pos);
	}

	@SafeVarargs
	
	public static boolean isReplaceable(BlockPos pos, Class<? extends Block>... replaceableBlocks) {
		return isReplaceable(pos, Arrays.asList(replaceableBlocks));
	}

	
	private static boolean isReplaceable(BlockPos pos, List<Class<? extends Block>> replaceableBlocks) {
		return BlockUtils.isReplaceable(pos) || isOf(pos, (replaceableBlocks));
	}

	public static List<Entity> surroundEntities(BlockPos pos) {
		ArrayList<Entity> empty = new ArrayList<>();

		if (mc.world == null) {
			return empty;
		}

		VoxelShape zeroShape = Blocks.OBSIDIAN
				.getDefaultState()
				.getCollisionShape(mc.world, pos, ShapeContext.absent());

		if (zeroShape.isEmpty()) {
			return empty;
		}

		VoxelShape shape = zeroShape.offset(pos.getX(), pos.getY(), pos.getZ());

		return WorldUtils.getEntities(shape.getBoundingBox(), entity -> {
			if (!entity.isSpectator() && entity.isAlive()) {
				return BlockUtils.isIntersects(shape, EntityType.END_CRYSTAL) != null;
			}
			return false;
		});
	}
}
