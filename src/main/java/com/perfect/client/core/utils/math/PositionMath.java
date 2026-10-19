package com.perfect.client.core.utils.math;

import com.perfect.client.core.utils.game.entity.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.Utils.add;


public class PositionMath {

	public static Box getBoxAt(Box box, Vec3d pos) {
		double dx = box.getLengthX() * 0.5;
		double dy = box.getLengthY();
		double dz = box.getLengthZ() * 0.5;
		return new Box(pos.x - dx, pos.y, pos.z - dz, pos.x + dx, pos.y + dy, pos.z + dz);
	}

	public static List<BlockPos> getBoxIntersectedMinimum(List<BlockPos> list, Box b) {
		add(list, BlockPos.ofFloored(b.minX, b.minY, b.minZ));
		add(list, BlockPos.ofFloored(b.minX, b.minY, b.maxZ));
		add(list, BlockPos.ofFloored(b.maxX, b.minY, b.minZ));
		add(list, BlockPos.ofFloored(b.maxX, b.minY, b.maxZ));
		return list;
	}

	public static List<BlockPos> getBoxIntersectedMaximum(List<BlockPos> list, Box b) {
		add(list, BlockPos.ofFloored(b.minX, b.maxY, b.minZ));
		add(list, BlockPos.ofFloored(b.minX, b.maxY, b.maxZ));
		add(list, BlockPos.ofFloored(b.maxX, b.maxY, b.minZ));
		add(list, BlockPos.ofFloored(b.maxX, b.maxY, b.maxZ));
		return list;
	}

	public static List<BlockPos> getIntersectedBlocks(Box b) {
		List<BlockPos> list = new ArrayList<>();

		getBoxIntersectedMinimum(list, b);
		getBoxIntersectedMaximum(list, b);

		return list;
	}

	public static Box boxFromCenter(double expand, Vec3d vec3d) {
		return boxFromCenter(expand, vec3d.x, vec3d.y, vec3d.z);
	}

	public static Box boxFromCenter(double expand, double x, double y, double z) {
		return new Box(x - expand, y - expand, z - expand, x + expand, y + expand, z + expand);
	}

	public static Box shrinkBox(Box box, double val) {
		return shrinkBox(box, val, val, val);
	}

	public static Box shrinkBox(Box box, double x, double y, double z) {
		double xShrink = box.getLengthX() * x * 0.5d;
		double yShrink = box.getLengthY() * y * 0.5d;
		double zShrink = box.getLengthZ() * z * 0.5d;

		return box.expand(-xShrink, -yShrink, -zShrink);
	}

	public static boolean intersectsOrCollides(Box b1, Box b2) {
		return b1.minX <= b2.maxX
				&& b1.maxX >= b2.minX
				&& b1.minY <= b2.maxY
				&& b1.maxY >= b2.minY
				&& b1.minZ <= b2.maxZ
				&& b1.maxZ >= b2.minZ;
	}

	public static Direction directionFromBlock(BlockPos from, BlockPos to) {
		BlockPos vector = to.subtract(from);
		return Direction.fromVector(vector, Direction.DOWN);
	}

	public static Vec3d closestVec3d(Vec3d vec3d, BlockPos pos) {
		return closestVec3d(vec3d, new Box(pos));
	}

	public static Vec3d closestVec3d(Entity entity, BlockPos pos) {
		return closestVec3d(entity, new Box(pos));
	}

	public static Vec3d closestVec3d(Entity entity, Box box) {
		if (box == null || entity == null) {
			return Vec3d.ZERO;
		}
		return closestVec3d(new Vec3d(
				entity.getX(),
				entity.getY() + entity.getEyeHeight(entity.getPose()),
				entity.getZ()
		), box);
	}

	public static Vec3d closestVec3d(Vec3d toPos, Box box) {
		if (box == null || toPos == null) {
			return Vec3d.ZERO;
		}

		double x = MathHelper.clamp(toPos.getX(), box.minX, box.maxX);
		double y = MathHelper.clamp(toPos.getY(), box.minY, box.maxY);
		double z = MathHelper.clamp(toPos.getZ(), box.minZ, box.maxZ);

		return new Vec3d(x, y, z);
	}

	public static boolean intersects(Box box, BlockState state, BlockPos pos) {
		VoxelShape shape = state.getCollisionShape(mc.world, pos, ShapeContext.absent());

		if (shape.isEmpty()) {
			return false;
		}

		return VoxelShapes.matchesAnywhere(
				shape.offset(pos.getX(), pos.getY(), pos.getZ()),
				VoxelShapes.cuboid(box),
				BooleanBiFunction.AND
		);
	}

	public static boolean isOutOfRange(BlockPos pos, double range) {
		return isOutOfRange(pos.toCenterPos(), range);
	}

	public static boolean isOutOfRange(Vec3d pos, double range) {
		return squareDistance(pos) > range * range;
	}

	public static boolean isOutOfRange(Vec3d center, Vec3d pos, double range) {
		return squareDistance(center, pos) > range * range;
	}

	public static double sqrtDistance(Vec3d pos1, Vec3d pos2) {
		return Math.sqrt(squareDistance(pos1, pos2));
	}

	public static double squareDistance(BlockPos pos1) {
		if (mc.player == null) {
			return 0;
		}
		return squareDistance(pos1.toCenterPos(), mc.player.getEyePos());
	}

	public static double squareDistance(Vec3d pos1) {
		if (mc.player == null) {
			return 0;
		}
		return squareDistance(pos1, mc.player.getEyePos());
	}

	public static double squareDistance(BlockPos pos1, Vec3d pos2) {
		return squareDistance(pos1.toCenterPos(), pos2);
	}

	public static double squareDistance(Vec3d pos1, Vec3d pos2) {
		double dx = pos1.x - pos2.x;
		double dy = pos1.y - pos2.y;
		double dz = pos1.z - pos2.z;
		return dx * dx + dy * dy + dz * dz;
	}

	public static double squareDistanceHorizontal(Vec3d pos1, Vec3d pos2) {
		double dx = pos1.x - pos2.x;
		double dz = pos1.z - pos2.z;
		return dx * dx + dz * dz;
	}

	public static double squareDistanceVertical(Vec3d pos1, Vec3d pos2) {
		double dz = pos1.y - pos2.y;
		return dz * dz;
	}

	public static int simpleDistance(Vec3i pos1, Vec3i pos2) {
		int x = Math.abs(pos1.getX() - pos2.getX());
		int y = Math.abs(pos1.getY() - pos2.getY());
		int z = Math.abs(pos1.getZ() - pos2.getZ());
		return x + y + z;
	}

	public static int simpleDistance(BlockPos pos1, BlockPos pos2) {
		int x = Math.abs(pos1.getX() - pos2.getX());
		int y = Math.abs(pos1.getY() - pos2.getY());
		int z = Math.abs(pos1.getZ() - pos2.getZ());
		return x + y + z;
	}

	public static int simpleDistanceHorizontal(Vec3i pos1, Vec3i pos2) {
		int x = Math.abs(pos1.getX() - pos2.getX());
		int z = Math.abs(pos1.getZ() - pos2.getZ());
		return x + z;
	}

	public static int maxSimpleDistance(Vec3i pos1, Vec3i pos2) {
		int x = Math.abs(pos1.getX() - pos2.getX());
		int y = Math.abs(pos1.getY() - pos2.getY());
		int z = Math.abs(pos1.getZ() - pos2.getZ());
		return Math.max(Math.max(x, y), z);
	}

	public static ArrayList<BlockPos> getSurroundingBlocks(BlockPos pos) {
		ArrayList<BlockPos> list = new ArrayList<>();

		for (Direction direction : Direction.values()) {
			BlockPos offsetPos = pos.offset(direction);
			list.add(offsetPos);
		}

		return list;
	}

	public static ArrayList<BlockPos> getCube(int range) {
		return getCube(range, range);
	}

	public static ArrayList<BlockPos> getCube(double rangeXZ, double rangeY) {
		return getCube((int) rangeXZ + 1, (int) rangeY + 1);
	}

	public static ArrayList<BlockPos> getCube(int rangeXZ, int rangeY) {
		if (mc.player == null) {
			return new ArrayList<>();
		}
		return getCube(EntityUtils.getEyePos(mc.player), rangeXZ, rangeY);
	}

	public static ArrayList<BlockPos> getCube(Vec3d center, int rangeXZ, int rangeY) {
		return getCube(BlockPos.ofFloored(center), rangeXZ, rangeY);
	}

	public static ArrayList<BlockPos> getCube(Vec3i center, int rangeXZ, int rangeY) {
		return getCube(center, rangeXZ, rangeY, null);
	}

	public static ArrayList<BlockPos> getCube(Vec3i center, int rangeXZ, int rangeY, Predicate<BlockPos> predicate) {
		ArrayList<BlockPos> list = new ArrayList<>();

		for (int x = -rangeXZ; x <= rangeXZ; ++x) {
			for (int y = -rangeY; y <= rangeY; ++y) {
				for (int z = -rangeXZ; z <= rangeXZ; ++z) {
					BlockPos pos = new BlockPos(center.getX() + x, center.getY() + y, center.getZ() + z);

					if (predicate != null && !predicate.test(pos)) {
						continue;
					}
					list.add(pos);
				}
			}
		}
		return list;
	}
}
