package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(LightmapTextureManager.class)
public class LightMapTextureManagerMixin {

	@Inject(
			method = "getDarkness(Lnet/minecraft/entity/LivingEntity;FF)F",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$getDarknessFactor(
			LivingEntity entity,
			float factor,
			float tickProgress,
			CallbackInfoReturnable<Float> cir
	) {
		if (noRender.contains(Elements.DARKNESS)) {
			cir.setReturnValue(0f);
		}
	}
}
