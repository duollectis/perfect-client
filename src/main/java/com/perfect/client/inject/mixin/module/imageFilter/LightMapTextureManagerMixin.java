package com.perfect.client.inject.mixin.module.imageFilter;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.perfect.client.functional.function.visuals.imageFilter.utils.CustomLightMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.imageFilter;

@Mixin(LightmapTextureManager.class)
public class LightMapTextureManagerMixin {

	@Inject(
			method = "getGlTextureView",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$getTextureView(CallbackInfoReturnable<GpuTextureView> cir) {
		if (CustomLightMap.isRendering()) {
			cir.setReturnValue(CustomLightMap.getTextureView());
		}
	}

	@Inject(
			method = "enable",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$enable(CallbackInfo ci) {
		if (CustomLightMap.isRendering()) {
			CustomLightMap.enable();
			ci.cancel();
		}
	}

	@Inject(
			method = "update",
			at = @At("HEAD")
	)
	private void perfect$update(CallbackInfo ci) {
		if (imageFilter.isEnabled()) {
			CustomLightMap.update();
		}
	}

	@Inject(
			method = "<init>",
			at = @At("TAIL")
	)
	private void perfect$init(GameRenderer gameRenderer, MinecraftClient minecraftClient, CallbackInfo ci) {
		CustomLightMap.create();
	}
}
