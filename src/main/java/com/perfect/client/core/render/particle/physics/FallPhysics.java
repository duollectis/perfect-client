package com.perfect.client.core.render.particle.physics;

import com.perfect.client.core.render.particle.fireFly.IParticlePhysics;
import com.perfect.client.core.utils.math.MathUtils;
import net.minecraft.util.math.Vec3d;

public class FallPhysics implements IParticlePhysics {

	@Override
	public Vec3d getStartVelocity(float speed) {
		float y = -MathUtils.random(0.0f, speed);
		return new Vec3d(0.0, y, 0.0).multiply(0.03);
	}

	@Override
	public Vec3d getVelocity(Vec3d position, Vec3d velocity) {
		return velocity.multiply(0.999999).subtract(0.0, 0.00005, 0.0);
	}
}
