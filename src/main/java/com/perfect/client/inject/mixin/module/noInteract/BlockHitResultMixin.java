package com.perfect.client.inject.mixin.module.noInteract;

import com.perfect.client.inject.MixinHooks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockHitResult.class)
public class BlockHitResultMixin {

	@Inject(
			method = "getType",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$getType(CallbackInfoReturnable<HitResult.Type> cir) {
		if (MixinHooks.isReplaceHitResult()) {
			cir.setReturnValue(HitResult.Type.MISS);
		}
	}
}
