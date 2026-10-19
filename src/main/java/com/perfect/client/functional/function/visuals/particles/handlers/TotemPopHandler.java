package com.perfect.client.functional.function.visuals.particles.handlers;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.particle.StandardParticle;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.functional.function.visuals.particles.Particles;
import com.perfect.client.functional.function.visuals.particles.ParticlesConfig;
import com.perfect.client.functional.function.visuals.particles.utils.ParticlesHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TotemPopHandler extends ParticlesHandler {

	public TotemPopHandler(
			Particles function,
			ParticlesConfig config
	) {
		super(function, config);
	}

	@Override
	public void onPlayerTotemPop(PlayerEvent event) {
		PlayerEntity player = event.getPlayer();

		spawnTotemParticles(player);
	}

	private void spawnTotemParticles(PlayerEntity player) {
		Vec3d pos = player.getPos();

		for (int i = 0; i < 50; i++) {
			Managers.PARTICLE.add(new StandardParticle(
					new Vec3d(
							pos.x,
							pos.y + MathUtils.random(0.0f, player.getHeight()),
							pos.z
					),
					modifySpeed(50.0),
					(float) config.getParticleScale().fromPercent(),
					new MultiColor(ColorUtils.generateTotemColor(config.getParticleColor().get().getAlpha()))
			));
		}
	}
}
