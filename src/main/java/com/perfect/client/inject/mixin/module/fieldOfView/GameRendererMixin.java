package com.perfect.client.inject.mixin.module.fieldOfView;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.fieldOfView;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(
			method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;",
					ordinal = 0
			),
			cancellable = true
	)
	public void perfect$getFov(
			Camera camera,
			float tickDelta,
			boolean changingFov,
			CallbackInfoReturnable<Float> cir
	) {
		if (fieldOfView.isEnabled()) {
			cir.setReturnValue(fieldOfView.getValue());
		}
	}
}