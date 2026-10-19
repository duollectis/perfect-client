package com.perfect.client.functional.function.visuals.particles.handlers;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.particle.StandardParticle;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.visuals.particles.Particles;
import com.perfect.client.functional.function.visuals.particles.Particles.Condition;
import com.perfect.client.functional.function.visuals.particles.ParticlesConfig;
import com.perfect.client.functional.function.visuals.particles.utils.ParticlesHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Vec3d;

public class ProjectileHandler extends ParticlesHandler {

	public ProjectileHandler(
			Particles function,
			ParticlesConfig config
	) {
		super(function, config);
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null || mc.player == null || mc.gameRenderer.getCamera() == null) {
			return;
		}

		spawnProjectileParticles();
	}

	private void spawnProjectileParticles() {
		assert mc.world != null;

		for (Entity entity : mc.world.getEntities()) {
			if (!isPearl(entity) && !isValidProjectile(entity)) {
				continue;
			}

			Vec3d pos = entity.getPos();

			Managers.PARTICLE.add(new StandardParticle(
					new Vec3d(pos.x, pos.y + entity.getHeight() / 2.0f, pos.z),
					modifySpeed(50.0),
					(float) config.getParticleScale().fromPercent(),
					config.getParticleColor().get()
			));
		}
	}

	private boolean isPearl(Entity entity) {
		return shouldSpawn(Condition.PEARL) && entity instanceof EnderPearlEntity;
	}

	private boolean isValidProjectile(Entity entity) {
		if (!EntityUtils.isMoving(entity)) {
			return false;
		}

		return isTrident(entity) || isArrow(entity);
	}

	private boolean isTrident(Entity entity) {
		return shouldSpawn(Condition.TRIDENT) && entity instanceof TridentEntity;
	}

	private boolean isArrow(Entity entity) {
		return shouldSpawn(Condition.BOW) && entity instanceof ArrowEntity;
	}
}
