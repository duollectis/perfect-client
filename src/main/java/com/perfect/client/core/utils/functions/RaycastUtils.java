package com.perfect.client.core.utils.functions;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

import static com.perfect.client.PerfectClient.mc;

public class RaycastUtils {

	public static BlockHitResult createMissed() {
		if (mc.player == null) {
			return null;
		}
		return BlockHitResult.createMissed(mc.player.getPos(), Direction.UP, mc.player.getBlockPos());
	}

	public static Vec3d getVec3dFromVector(Vec3d center, Vec3d vector, double distance) {
		return center.add(vector.x * distance, vector.y * distance, vector.z * distance);
	}

	public static Box getBox(Vec3d start, Vec3d end) {
		return new Box(
				Math.min(start.x, end.x),
				Math.min(start.y, end.y),
				Math.min(start.z, end.z),
				Math.max(start.x, end.x),
				Math.max(start.y, end.y),
				Math.max(start.z, end.z)
		);
	}

	public static Optional<Vec3d> raycast(Box box, Vec3d from, Vec3d to) {
		if (box.contains(from)) {
			return Optional.of(from);
		}
		return box.raycast(from, to);
	}
}
