package com.perfect.client.core.render.particle.fireFly;

import net.minecraft.util.math.Vec3d;

public interface IParticlePhysics {

	Vec3d getStartVelocity(float speed);

	Vec3d getVelocity(Vec3d position, Vec3d velocity);
}
