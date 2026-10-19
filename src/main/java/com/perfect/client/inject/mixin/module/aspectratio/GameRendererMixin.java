package com.perfect.client.inject.mixin.module.aspectRatio;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.aspectRatio;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	private float viewDistanceBlocks;

	@Inject(
			method = "getBasicProjectionMatrix",
			at = @At("TAIL"),
			cancellable = true
	)
	public void perfect$getBasicProjectionMatrixHook(float fov, CallbackInfoReturnable<Matrix4f> cir) {
		if (aspectRatio.isEnabled()) {
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.peek().getPositionMatrix().identity();
			matrixStack.peek().getPositionMatrix().mul(new Matrix4f().setPerspective(
					(float) (fov * 0.01745329238474369),
					aspectRatio.getRatio(),
					0.05f,
					viewDistanceBlocks * 4.0f
			));

			cir.setReturnValue(matrixStack.peek().getPositionMatrix());
		}
	}
}
