package com.perfect.client.core.utils.math.damage;

import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.inject.mixinInterface.IVec3d;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3f;

import java.util.List;

@Getter

public class ExposureCalculator {

	private final RaycastCalculator raycastCalculator = new RaycastCalculator();

	public float getExposure(
			Vec3d source,
			Entity entity,
			Box box,
			boolean ignoreTerrain,
			boolean placing,
			BlockPos ignored,
			List<BlockPos> support
	) {
		Vector3f reciprocalSize = new Vector3f(
				1f / (float) ((box.maxX - box.minX) * 2f + 1f),
				1f / (float) ((box.maxY - box.minY) * 2f + 1f),
				1f / (float) ((box.maxZ - box.minZ) * 2f + 1f)
		);

		if (reciprocalSize.x < 0f || reciprocalSize.y < 0f || reciprocalSize.z < 0f) {
			return 0f;
		}

		double g = (1d - Math.floor(1d / reciprocalSize.x) * reciprocalSize.x) * 0.5;
		double h = (1d - Math.floor(1d / reciprocalSize.z) * reciprocalSize.z) * 0.5;

		int nonSolid = 0;
		int total = 0;

		Vec3d vec3d = new Vec3d(0, 0, 0);
		IVec3d iVec3d = (IVec3d) vec3d;

		RaycastContext raycastContext = new RaycastContext(
				vec3d,
				source,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				entity
		);

		for (float x = 0f; x <= 1f; x = (x + reciprocalSize.x)) {
			double lerpX = MathHelper.lerp(x, box.minX, box.maxX);
			iVec3d.perfect$setX(lerpX + g);

			for (float y = 0f; y <= 1f; y = (float) ((double) y + reciprocalSize.y)) {
				double lerpY = MathHelper.lerp(y, box.minY, box.maxY);
				iVec3d.perfect$setY(lerpY);

				for (float z = 0f; z <= 1f; z = (float) ((double) z + reciprocalSize.z)) {
					double lerpZ = MathHelper.lerp(z, box.minZ, box.maxZ);
					iVec3d.perfect$setZ(lerpZ + h);

					BlockHitResult raycast = raycastCalculator.getRaycastResult(
							raycastContext,
							ignoreTerrain,
							placing,
							ignored,
							support
					);

					if (HitResultUtils.isMiss(raycast)) {
						++nonSolid;
					}

					++total;
				}
			}
		}

		return (float) nonSolid / (float) total;
	}
}
