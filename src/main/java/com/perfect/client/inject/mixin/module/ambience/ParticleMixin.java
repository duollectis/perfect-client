package com.perfect.client.inject.mixin.module.ambience;

import com.perfect.client.core.utils.math.MathUtils;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static com.perfect.client.manager.system.FunctionManager.ambience;

@Mixin(Particle.class)
public abstract class ParticleMixin {

	@Shadow
	protected float red;

	@Shadow
	protected float green;

	@Shadow
	protected float blue;

	@Shadow
	protected float alpha;

	@Shadow
	public abstract void setColor(float red, float green, float blue);

	@Shadow
	protected abstract void setAlpha(float alpha);

	@Inject(
			method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDD)V",
			at = @At("TAIL")
	)
	public void perfect$init(ClientWorld world, double x, double y, double z, CallbackInfo ci) {
		if (ambience.isEnabled() && ambience.getParticles()) {
			setColor(1f, 1f, 1f);
			setAlpha(1f);
		}
	}

	@Inject(
			method = "setColor",
			at = @At("TAIL")
	)
	public void perfect$setColor(float red, float green, float blue, CallbackInfo ci) {
		if (ambience.isEnabled() && ambience.getParticles()) {
			Color color = ambience.getParticleColor().getOffsetColor(MathUtils.random(0f, 1f));

			this.red = color.getRed() / 255f;
			this.green = color.getGreen() / 255f;
			this.blue = color.getBlue() / 255f;
		}
	}

	@Inject(
			method = "setAlpha",
			at = @At("TAIL")
	)
	public void perfect$setAlpha(float alpha, CallbackInfo ci) {
		if (ambience.isEnabled() && ambience.getParticles()) {
			this.alpha = ambience.getParticleColor().getAlpha();
		}
	}
}
