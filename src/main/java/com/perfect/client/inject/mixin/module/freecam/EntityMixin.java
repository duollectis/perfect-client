package com.perfect.client.inject.mixin.module.freecam;

import com.perfect.client.core.utils.mixin.MixinUtils;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.freecam;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(
			method = "changeLookDirection",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$updateChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		if (MixinUtils.isClientPlayer(this) && freecam.isEnabled() && !freecam.isPlayerControl()) {
			ci.cancel();
			freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
		}
	}
}
