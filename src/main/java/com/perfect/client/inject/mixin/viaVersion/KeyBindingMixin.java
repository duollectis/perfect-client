package com.perfect.client.inject.mixin.viaVersion;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.PerfectClient.mc;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	@Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
	public void perfect$isPressed(CallbackInfoReturnable<Boolean> cir) {
		if(mc.player != null && (Object) this == mc.options.sprintKey) {
			cir.setReturnValue(mc.player.input.playerInput.sprint());
		}
	}
}
