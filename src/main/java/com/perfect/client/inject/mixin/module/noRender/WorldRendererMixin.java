package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(
			method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$hasBlindnessOrDarkness(Camera camera, CallbackInfoReturnable<Boolean> info) {
		if (noRender.contains(Elements.BLINDNESS) || noRender.contains(Elements.DARKNESS)) {
			info.setReturnValue(null);
		}
	}
}
