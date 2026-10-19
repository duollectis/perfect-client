package com.perfect.client.core.utils.functions.interact;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.inject.mixinInterface.IVec3d;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import java.util.List;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.game.world.BlockUtils.hasEntity;


public class HitResultUtils {

	public static boolean isMiss(BlockHitResult result) {
		return result == null || result.getType() == HitResult.Type.MISS;
	}

	public static BlockHitResult getPlaceResult(BlockPos pos) {
		if (mc.player == null) {
			return null;
		}
		return getPlaceResult(pos, mc.player.getEyePos(), null, null);
	}

	public static BlockHitResult getPlaceResult(BlockPos pos, List<BlockPos> pending) {
		if (mc.player == null) {
			return null;
		}
		return getPlaceResult(pos, mc.player.getEyePos(), pending, null);
	}

	public static BlockHitResult getPlaceResult(
			BlockPos pos,
			Vec3d bestClosest,
			List<BlockPos> pending,
			List<BlockPos> ignored
	) {
		if (!isReplaceable(pos, ignored)) {
			return null;
		}

		FindDirectionResult result = getPlaceDirectionResult(pos, bestClosest, pending, ignored);

		if (clientConfig.getAirPlace()) {
			FindDirectionResult airPlaceResult = getAirPlaceResult(result, pos, bestClosest, ignored);

			if (airPlaceResult != null) {
				result = airPlaceResult;
			}
		}

		if (result == null) {
			return null;
		}

		return new BlockHitResult(result.vec3d(), result.direction(), result.interactPos(), false);
	}

	private static FindDirectionResult getAirPlaceResult(
			FindDirectionResult result,
			BlockPos pos,
			Vec3d bestClosest,
			List<BlockPos> ignored
	) {
		double distance = PositionMath.squareDistance(pos);

		if (result != null && result.distance() <= distance) {
			return null;
		}

		return new FindDirectionResult(
				getAirPlaceDirection(pos, ignored),
				pos,
				PositionMath.closestVec3d(bestClosest, pos),
				distance
		);
	}

	private static FindDirectionResult getPlaceDirectionResult(
			BlockPos pos,
			Vec3d bestClosest,
			List<BlockPos> pending,
			List<BlockPos> ignored
	) {
		FindDirectionResult bestDirection = null;
		double bestRange = -1;

		for (Direction direction : Direction.values()) {
			BlockPos offsetPos = pos.offset(direction);
			Direction opposite = direction.getOpposite();

			if (isReplaceable(offsetPos, ignored) &&
					(pending == null || !pending.contains(offsetPos))
					&& !Managers.PENDING.contains(offsetPos)) {
				continue;
			}

			Vec3d vec3d = getVec3d(offsetPos, opposite, bestClosest);

			if (!isGoodDirection(offsetPos, vec3d, opposite, pending, ignored)) {
				continue;
			}

			double range = PositionMath.squareDistance(vec3d);

			if (bestRange == -1 || range < bestRange) {
				bestRange = range;
				bestDirection = new FindDirectionResult(opposite, offsetPos, vec3d, range);
			}
		}

		return bestDirection;
	}

	private static boolean isGoodDirection(
			BlockPos offsetPos,
			Vec3d vec3d,
			Direction opposite,
			List<BlockPos> pending,
			List<BlockPos> ignored
	) {
		if (!clientConfig.getStrictDirections()) {
			return true;
		}
		return isPlaceDirectionStrict(offsetPos, vec3d, opposite, pending, ignored);
	}

	public static Vec3d getVec3d(BlockPos pos, Direction direction, Vec3d bestClosest) {
		Vec3d center = pos.toCenterPos();
		Vec3d closest = PositionMath.closestVec3d(bestClosest, new Box(pos).expand(-0.05));
		Vec3i vector = direction.getVector();

		if (vector.getX() > 0) {
			((IVec3d) closest).perfect$setX(center.x + 0.5);
		}
		else if (vector.getX() < 0) {
			((IVec3d) closest).perfect$setX(center.x - 0.5);
		}

		else if (vector.getY() > 0) {
			((IVec3d) closest).perfect$setY(center.y + 0.5);
		}
		else if (vector.getY() < 0) {
			((IVec3d) closest).perfect$setY(center.y - 0.5);
		}

		else if (vector.getZ() > 0) {
			((IVec3d) closest).perfect$setZ(center.z + 0.5);
		}
		else if (vector.getZ() < 0) {
			((IVec3d) closest).perfect$setZ(center.z - 0.5);
		}

		return closest;
	}

	public static boolean isPlaceDirectionStrict(
			BlockPos pos,
			Vec3d vec3d,
			Direction direction,
			List<BlockPos> pending,
			List<BlockPos> ignored
	) {
		if (mc.player == null) {
			return false;
		}
		Vec3d eyePos = mc.player.getEyePos();

		return switch (direction) {
			case UP -> notSolid(pos.offset(direction), pending, ignored) && eyePos.y > vec3d.y;
			case DOWN -> notSolid(pos.offset(direction), pending, ignored) && eyePos.y < vec3d.y;
			case EAST -> notSolid(pos.offset(direction), pending, ignored) && eyePos.x > vec3d.x;
			case WEST -> notSolid(pos.offset(direction), pending, ignored) && eyePos.x < vec3d.x;
			case SOUTH -> notSolid(pos.offset(direction), pending, ignored) && eyePos.z > vec3d.z;
			case NORTH -> notSolid(pos.offset(direction), pending, ignored) && eyePos.z < vec3d.z;
		};
	}

	public static BlockHitResult getInteractResult(BlockPos pos) {
		return getInteractResult(pos, null);
	}

	public static BlockHitResult getInteractResult(BlockPos pos, BlockHitResult rayTracePos) {
		if (mc.player == null) {
			return null;
		}

		if (rayTracePos != null) {
			return rayTracePos;
		}

		if (!clientConfig.getStrictDirections()) {
			return new BlockHitResult(
					PositionMath.closestVec3d(mc.player.getEyePos(), pos),
					Direction.DOWN,
					pos,
					false
			);
		}

		BlockHitResult bestResult = null;
		double bestRange = -1;

		for (Direction direction : Direction.values()) {
			Vec3d vec3d = getVec3d(pos, direction, mc.player.getEyePos());

			if (!isInteractDirectionStrict(vec3d, direction)) {
				continue;
			}

			double range = PositionMath.squareDistance(vec3d);

			if (bestRange == -1 || range < bestRange) {
				bestRange = range;
				bestResult = new BlockHitResult(vec3d, direction, pos, false);
			}
		}

		return bestResult;
	}

	public static boolean isInteractDirectionStrict(
			Vec3d vec3d,
			Direction direction
	) {
		if (mc.player == null) {
			return false;
		}
		Vec3d eyePos = mc.player.getEyePos();

		return switch (direction) {
			case UP -> eyePos.y > vec3d.y;
			case DOWN -> eyePos.y < vec3d.y;
			case EAST -> eyePos.x > vec3d.x;
			case WEST -> eyePos.x < vec3d.x;
			case SOUTH -> eyePos.z > vec3d.z;
			case NORTH -> eyePos.z < vec3d.z;
		};
	}

	public static Direction getAirPlaceDirection(BlockPos pos, List<BlockPos> ignored) {
		for (Direction direction : Direction.values()) {
			if (!isReplaceable(pos.offset(direction), ignored) || hasEntity(pos)) {
				return direction.getOpposite();
			}
		}
		return Direction.DOWN;
	}

	private static boolean notSolid(BlockPos pos, List<BlockPos> pending, List<BlockPos> ignored) {
		return !BlockUtils.isSolid(pos)
				|| Managers.PENDING.contains(pos)
				|| (pending != null && pending.contains(pos))
				|| (ignored != null && ignored.contains(pos));
	}

	private static boolean isReplaceable(BlockPos pos, List<BlockPos> ignored) {
		if (ignored != null && ignored.contains(pos)) {
			return true;
		}
		return BlockUtils.isReplaceable(pos);
	}

	public record FindDirectionResult(
			Direction direction,
			BlockPos interactPos,
			Vec3d vec3d,
			double distance
	) {
	}
}
