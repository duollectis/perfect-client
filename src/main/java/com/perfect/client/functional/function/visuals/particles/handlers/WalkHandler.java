package com.perfect.client.functional.function.visuals.particles.handlers;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.particle.StandardParticle;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.functional.function.visuals.particles.Particles;
import com.perfect.client.functional.function.visuals.particles.ParticlesConfig;
import com.perfect.client.functional.function.visuals.particles.utils.ParticlesHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class WalkHandler extends ParticlesHandler {

	public WalkHandler(
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

		spawnWalkingParticles();
	}

	private void spawnWalkingParticles() {
		assert mc.player != null;

		PlayerEntity player = mc.player;

		if (!EntityUtils.isMoving(player) || !isEvenTick(player)) {
			return;
		}

		Vec3d pos = player.getPos();
		float width = player.getWidth();
		float height = player.getHeight();

		for (int i = 0; i < 2; i++) {
			Vec3d particlePos = createParticlePosition(player, pos, width, height);

			Managers.PARTICLE.add(new StandardParticle(
					particlePos,
					modifySpeed(70.0),
					(float) config.getParticleScale().fromPercent(),
					config.getParticleColor().get()
			));
		}
	}

	private boolean isEvenTick(PlayerEntity player) {
		return player.age % 2 == 0;
	}

	private Vec3d createParticlePosition(PlayerEntity player, Vec3d pos, float width, float height) {
		float dx = player.getMovementDirection().getOpposite().getOffsetX() * (width / 2.0f);
		float dz = player.getMovementDirection().getOpposite().getOffsetZ() * (width / 2.0f);

		return new Vec3d(
				pos.x + dx,
				pos.y + MathUtils.random(0.2f, height),
				pos.z + dz
		);
	}
}
