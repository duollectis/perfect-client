package com.perfect.client.functional.function.visuals.killeffect.modes;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.functional.function.visuals.killeffect.KillEffect;
import com.perfect.client.functional.function.visuals.killeffect.KillEffectConfig;
import com.perfect.client.functional.function.visuals.killeffect.data.KillEffectMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;

public class ParticlesMode extends KillEffectMode {

	public ParticlesMode(
			KillEffect function,
			KillEffectConfig config
	) {
		super(function, config);
	}

	@Override
	public void onPlayerDeath(PlayerEvent event) {
		if (mc.world == null) {
			return;
		}

		PlayerEntity player = event.getPlayer();

		for (int i = 0; i < 360; i += 5) {
			double sin = Math.sin(Math.toRadians(i)) * player.getWidth() * 1.2;
			double cos = Math.cos(Math.toRadians(i)) * player.getWidth() * 1.2;

			mc.world.addParticleClient(
					ParticleTypes.LAVA,
					player.getX() + cos,
					player.getY() + player.getHeight() * 0.75,
					player.getZ() + sin,
					0.0, 0.0, 0.0
			);
		}
	}
}