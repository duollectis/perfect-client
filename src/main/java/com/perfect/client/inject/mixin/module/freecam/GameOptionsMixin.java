package com.perfect.client.inject.mixin.module.freecam;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.freecam;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

	@Inject(
			method = "setPerspective",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$setPerspective(Perspective perspective, CallbackInfo info) {
		if (freecam.isEnabled()) {
			info.cancel();
		}
	}
}
