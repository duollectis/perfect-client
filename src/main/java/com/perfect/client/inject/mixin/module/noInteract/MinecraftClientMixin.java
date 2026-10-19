package com.perfect.client.inject.mixin.module.noInteract;

import com.perfect.client.inject.MixinHooks;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.noInteract;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(
			method = "doItemUse",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"
			)
	)
	public void perfect$beforeGetType(CallbackInfo ci) {
		if (noInteract.shouldCancelInteract()) {
			MixinHooks.setReplaceHitResult(true);
		}
	}

	@Inject(
			method = "doItemUse",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;",
					shift = At.Shift.AFTER
			)
	)
	public void perfect$afterGetType(CallbackInfo ci) {
		MixinHooks.setReplaceHitResult(false);
	}
}
