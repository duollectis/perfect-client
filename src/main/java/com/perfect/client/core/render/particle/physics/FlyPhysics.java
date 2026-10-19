package com.perfect.client.core.render.particle.physics;

import com.perfect.client.core.render.particle.fireFly.IParticlePhysics;
import com.perfect.client.core.utils.math.MathUtils;
import net.minecraft.util.math.Vec3d;

public class FlyPhysics implements IParticlePhysics {

	@Override
	public Vec3d getStartVelocity(float speed) {
		float x = MathUtils.random(-speed, speed);
		float y = MathUtils.random(-0.1f, 0.1f);
		float z = MathUtils.random(-speed, speed);

		return new Vec3d(x, y, z).multiply(0.4);
	}

	@Override
	public Vec3d getVelocity(Vec3d position, Vec3d velocity) {
		return velocity.multiply(0.9);
	}
}
