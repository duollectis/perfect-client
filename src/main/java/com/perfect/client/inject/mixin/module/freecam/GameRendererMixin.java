package com.perfect.client.inject.mixin.module.freecam;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.Cancellable;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.freecam;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Inject(
			method = "updateCrosshairTarget",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$updateCrosshairTarget(float tickDelta, CallbackInfo info) {
		Cancellable event = new Cancellable();

		PerfectClient.EVENT_MANAGER.onUpdateCrosshairTarget(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(
			method = "renderHand",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
		if (freecam.noHands()) {
			ci.cancel();
		}
	}
}
