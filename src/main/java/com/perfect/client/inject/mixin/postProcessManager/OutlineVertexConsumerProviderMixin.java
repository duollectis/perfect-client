package com.perfect.client.inject.mixin.postProcessManager;

import com.perfect.client.core.render.postprocess.PostProcessManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OutlineVertexConsumerProvider.class)
public class OutlineVertexConsumerProviderMixin {

	@Inject(method = "draw()V", at = @At("HEAD"))
	public void draw(CallbackInfo ci) {
		PostProcessManager.endWrite();
	}
}
