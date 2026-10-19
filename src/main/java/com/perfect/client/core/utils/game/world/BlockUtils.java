package com.perfect.client.core.utils.game.world;

import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.perfect.client.PerfectClient.mc;


public class BlockUtils {

	private static final List<Block> SHIFT_INTERACT_BLOCKS = Arrays.asList(
			Blocks.ENDER_CHEST,
			Blocks.CHEST,
			Blocks.TRAPPED_CHEST,
			Blocks.CRAFTING_TABLE,
			Blocks.BIRCH_TRAPDOOR,
			Blocks.BAMBOO_TRAPDOOR,
			Blocks.DARK_OAK_TRAPDOOR,
			Blocks.CHERRY_TRAPDOOR,
			Blocks.ANVIL,
			Blocks.BREWING_STAND,
			Blocks.HOPPER,
			Blocks.DROPPER,
			Blocks.DISPENSER,
			Blocks.ACACIA_TRAPDOOR,
			Blocks.ENCHANTING_TABLE,
			Blocks.WHITE_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.LIGHT_BLUE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.BLACK_SHULKER_BOX
	);

	public static Entity isIntersects(VoxelShape shape, EntityType<?> ignored) {
		if (mc.world == null || shape.isEmpty())
			return null;

		for (Entity entity : WorldUtils.getEntities(shape.getBoundingBox(), entity -> entity.getType() != ignored)) {
			if (entity.isRemoved() || !entity.intersectionChecked || !VoxelShapes.matchesAnywhere(
					shape,
					VoxelShapes.cuboid(entity.getBoundingBox()),
					BooleanBiFunction.AND
			)) {
				continue;
			}
			return entity;
		}

		return null;
	}

	public static boolean isOutOfBorder(BlockPos pos) {
		if (mc.world == null) {
			return false;
		}
		return mc.world.isOutOfHeightLimit(pos.getY()) || !mc.world.getWorldBorder().contains(pos);
	}

	public static boolean isInWeb(Box box) {
		for (BlockPos pos : PositionMath.getIntersectedBlocks(box)) {
			if (BlockUtils.isOf(pos, Blocks.COBWEB)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBlock(BlockPos pos) {
		return isBlock(getState(pos));
	}

	@SuppressWarnings("deprecation")
	public static boolean isBlock(BlockState state) {
		return !state.isAir() && !state.isLiquid();
	}

	public static boolean isCollidable(BlockPos pos) {
		return isCollidable(getState(pos));
	}

	public static boolean isCollidable(BlockState state) {
		return isCollidable(getBlock(state));
	}

	public static boolean isCollidable(Block block) {
		return block.collidable;
	}

	public static Block getBlock(BlockPos pos) {
		return getBlock(getState(pos));
	}

	public static Block getBlock(BlockState state) {
		return state.getBlock();
	}

	public static boolean needShiftInteract(BlockPos pos) {
		return SHIFT_INTERACT_BLOCKS.contains(getBlock(pos));
	}

	public static boolean needShiftInteract(BlockState state) {
		return SHIFT_INTERACT_BLOCKS.contains(getBlock(state));
	}

	public static Vec3d getCrystalVec3d(BlockPos pos) {
		return new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
	}

	public static boolean isBlastResist(BlockPos pos) {
		return getBlock(pos).getBlastResistance() >= 600;
	}

	public static boolean isBlastResist(BlockState state) {
		return getBlock(state).getBlastResistance() >= 600;
	}

	public static boolean isBlastResist(Block block) {
		return block.getBlastResistance() >= 600;
	}

	public static boolean isAir(BlockPos pos) {
		return getState(pos).isAir();
	}

	public static boolean isOf(BlockPos pos, Block block) {
		return getState(pos).isOf(block);
	}

	public static boolean isOf(BlockPos pos, Class<?> klass) {
		return klass.isInstance(getState(pos).getBlock());
	}

	public static boolean isOf(BlockPos pos, Fluid fluid) {
		return getState(pos).getFluidState().isOf(fluid);
	}

	public static boolean isOf(BlockPos pos, Block... blocks) {
		for (Block block : blocks) {
			if (isOf(pos, block))
				return true;
		}

		return false;
	}

	public static boolean isOf(BlockPos pos, List<Class<? extends Block>> classes) {
		for (Class<? extends Block> klass : classes) {
			if (isOf(pos, klass))
				return true;
		}

		return false;
	}

	public static boolean isSolid(BlockPos pos) {
		return getState(pos).isSolidBlock(mc.world, pos);
	}

	public static float getHardness(BlockPos pos) {
		return getHardness(pos, getState(pos));
	}

	public static float getHardness(BlockPos pos, BlockState state) {
		return state.getHardness(mc.world, pos);
	}

	public static boolean isBreakable(BlockPos pos) {
		return isBreakable(getHardness(pos));
	}

	public static boolean isBreakable(float hardness) {
		return hardness != -1f;
	}

	public static boolean isReplaceable(BlockPos pos) {
		return getState(pos).isReplaceable();
	}

	public static BlockState getState(BlockPos pos) {
		if (mc.world == null) {
			return Blocks.AIR.getDefaultState();
		}
		return mc.world.getBlockState(pos);
	}

	public static boolean hasEntity(BlockPos pos) {
		return hasEntity(new Box(pos));
	}

	public static boolean hasEntity(Box box) {
		return hasEntity(box, entity -> entity.intersectionChecked);
	}

	public static boolean hasEntity(BlockPos pos, Predicate<Entity> predicate) {
		return hasEntity(new Box(pos), predicate);
	}

	public static boolean hasEntity(Box box, Predicate<Entity> predicate) {
		return !getEntities(box, predicate).isEmpty();
	}

	public static List<Entity> getEntities(BlockPos pos) {
		return getEntities(
				pos, entity -> entity instanceof PlayerEntity
						|| entity instanceof EndCrystalEntity
						|| entity instanceof TntEntity
		);
	}

	public static List<Entity> getEntities(BlockPos pos, Predicate<Entity> predicate) {
		return getEntities(new Box(pos), predicate);
	}

	public static List<Entity> getEntities(Box box) {
		return getEntities(
				box, entity -> entity instanceof PlayerEntity
						|| entity instanceof EndCrystalEntity
						|| entity instanceof TntEntity
		);
	}

	public static List<Entity> getEntities(Box box, Predicate<Entity> predicate) {
		return WorldUtils.getEntities(box, predicate);
	}

	public static boolean canPlace(BlockPos pos) {
		if (mc.world == null) {
			return false;
		}
		return mc.world.canPlace(mc.world.getBlockState(pos), pos, ShapeContext.absent());
	}

	public static Box getOutlineShapeBox(BlockPos pos) {
		return getOutlineShapeBox(pos, getState(pos));
	}

	public static Box getOutlineShapeBox(BlockPos pos, BlockState state) {
		VoxelShape shape = getOutlineShape(pos, state);
		return getOutlineShapeBox(pos, shape);
	}

	public static Box getOutlineShapeBox(BlockPos pos, VoxelShape shape) {
		if (shape.isEmpty()) {
			return null;
		}
		return shape.getBoundingBox().offset(pos);
	}

	public static VoxelShape getOutlineShape(BlockPos pos) {
		return getOutlineShape(pos, getState(pos));
	}

	public static VoxelShape getOutlineShape(BlockPos pos, BlockState state) {
		return state.getOutlineShape(mc.world, pos);
	}
}
