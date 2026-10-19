package com.perfect.client.functional.function.visuals.particles.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.visuals.particles.Particles;
import com.perfect.client.functional.function.visuals.particles.ParticlesConfig;

public abstract class ParticlesHandler extends FunctionHandler<Particles, ParticlesConfig> {

	public ParticlesHandler(Particles function, ParticlesConfig config) {
		super(function, config);
	}

	protected double modifySpeed(double baseSpeed) {
		return 0.5 * (baseSpeed * (0.5 + (1 - config.getParticleSpeed().fromPercent())));
	}

	
	protected boolean shouldSpawn(Particles.Condition condition) {
		return config.getElements().get(condition);
	}
}
