package com.perfect.client.inject.mixin.module.hurtCam;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
			method = "tiltViewWhenHurt",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	public void perfect$tiltViewWhenHurt(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
		if (noRender.contains(Elements.HURT_CAM)) {
			ci.cancel();
		}
	}
}
