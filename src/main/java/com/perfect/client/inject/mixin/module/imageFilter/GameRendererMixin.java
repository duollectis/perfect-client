package com.perfect.client.inject.mixin.module.imageFilter;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	/*@Inject(
			method = "render",
			at = @At("TAIL")
	)
	private void perfect$renderTailHook(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		CustomLightMap.setRendering(false);
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/MinecraftClient;getFramebuffer()Lnet/minecraft/client/gl/Framebuffer;",
					ordinal = 0
			)
	)
	private void perfect$clear(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		if (imageFilter.isEnabled()) {
			CustomLightMap.setRendering(true);
		}
	}*/
}
