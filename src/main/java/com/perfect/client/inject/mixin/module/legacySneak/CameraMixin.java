package com.perfect.client.inject.mixin.module.legacySneak;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.legacySneak;

@Mixin(Camera.class)
public class CameraMixin {

	@Shadow
	private Entity focusedEntity;

	@Shadow
	private float lastCameraY;

	@Shadow
	private float cameraY;

	@Inject(
			method = "updateEyeHeight",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$updateEyeHeight(CallbackInfo ci) {
		if (legacySneak.isEnabled()) {
			ci.cancel();

			if (focusedEntity != null) {
				lastCameraY = cameraY;
				cameraY += focusedEntity.getStandingEyeHeight() - cameraY;
			}
		}
	}
}
