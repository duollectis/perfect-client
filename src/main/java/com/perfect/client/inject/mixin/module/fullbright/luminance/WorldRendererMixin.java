package com.perfect.client.inject.mixin.module.fullBright.luminance;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.perfect.client.manager.system.FunctionManager.fullBright;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@ModifyVariable(
			method = "getLightmapCoordinates(Lnet/minecraft/client/render/WorldRenderer$BrightnessGetter;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
			at = @At(value = "STORE"),
			ordinal = 0
	)
	private static int perfect$modifySkyLight(int skyLight) {
		return fullBright.getLuminance(true, skyLight);
	}

	@ModifyVariable(
			method = "getLightmapCoordinates(Lnet/minecraft/client/render/WorldRenderer$BrightnessGetter;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
			at = @At(value = "STORE"),
			ordinal = 1
	)
	private static int perfect$modifyBlockLight(int blockLight) {
		return fullBright.getLuminance(false, blockLight);
	}
}
