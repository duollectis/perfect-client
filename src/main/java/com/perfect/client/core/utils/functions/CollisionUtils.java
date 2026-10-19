package com.perfect.client.core.utils.functions;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class CollisionUtils {

	public static Vec3d adjustMovementForCollisions(
			Entity entity,
			Box box,
			Vec3d movement,
			float stepHeight,
			boolean onGround
	) {
		if (mc.world == null) {
			return Vec3d.ZERO;
		}

		List<VoxelShape> collisions = mc.world.getEntityCollisions(entity, box.stretch(movement));

		Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(
				entity,
				movement,
				box,
				mc.world,
				collisions
		);

		boolean movingX = movement.x != vec3d.x;
		boolean movingY = movement.y != vec3d.y;
		boolean movingZ = movement.z != vec3d.z;
		boolean falling = movingY && movement.y < 0.0;

		if (stepHeight > 0.0f && (falling || onGround) && (movingX || movingZ)) {
			Box fallBox = falling ? box.offset(0.0, vec3d.y, 0.0) : box;
			Box stepBox = fallBox.stretch(movement.x, stepHeight, movement.z);

			if (!falling) {
				stepBox = stepBox.stretch(0.0, -1.0e-5, 0.0);
			}

			List<VoxelShape> stepCollisions = Entity.findCollisionsForMovement(entity, mc.world, collisions, stepBox);

			float[] stepHeights = Entity.collectStepHeights(fallBox, stepCollisions, stepHeight, (float) vec3d.y);

			for (float step : stepHeights) {
				Vec3d stepMovement = Entity.adjustMovementForCollisions(
						new Vec3d(movement.x, step, movement.z),
						fallBox,
						stepCollisions
				);
				if (stepMovement.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
					double d = box.minY - fallBox.minY;
					return stepMovement.subtract(0.0, d, 0.0);
				}
			}
		}

		return vec3d;
	}
}
