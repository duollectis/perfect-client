package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

	@Shadow
	@Nullable
	protected abstract <T extends ParticleEffect> Particle createParticle(
			T parameters,
			double x,
			double y,
			double z,
			double velocityX,
			double velocityY,
			double velocityZ
	);

	@Inject(
			method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onAddParticle(
			ParticleEffect parameters,
			double x,
			double y,
			double z,
			double velocityX,
			double velocityY,
			double velocityZ,
			CallbackInfoReturnable<Particle> info
	) {
		ParticleEvent event = new ParticleEvent(parameters);
		PerfectClient.EVENT_MANAGER.onParticle(event);

		if (event.isCancelled()) {
			if (parameters.getType() == ParticleTypes.FLASH) {
				info.setReturnValue(createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ));
			}
			else {
				info.cancel();
			}
		}
	}
}
