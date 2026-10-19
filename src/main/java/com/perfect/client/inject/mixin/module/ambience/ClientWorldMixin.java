package com.perfect.client.inject.mixin.module.ambience;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.ambience;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@Inject(
			method = "getSkyColor",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onGetSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Integer> info) {
		if (ambience.isEnabled() && ambience.getCustomSkyColor()) {
			info.setReturnValue(ambience.skyColor().getRGB());
		}
	}
}
