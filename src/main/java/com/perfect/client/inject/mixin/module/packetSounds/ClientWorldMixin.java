package com.perfect.client.inject.mixin.module.packetSounds;

import com.perfect.client.inject.MixinHooks;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@Inject(
			method = "addBlockBreakParticles",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$addBreakParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
		if (MixinHooks.isCancelBreakParticles()) {
			MixinHooks.setCancelBreakParticles(false);
			ci.cancel();
		}
	}
}
