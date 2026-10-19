package com.perfect.client.inject.mixin.module.fullBright.luminance.indigo;

import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
		value = AoCalculator.class,
		remap = false
)
public class AoCalculatorMixin {

	/*@ModifyVariable(
			method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
			at = @At(value = "STORE"),
			ordinal = 0
	)
	private static int perfect$modifySkyLight(int skyLight) {
		return fullBright.getLuminance(true, skyLight);
	}

	@ModifyVariable(
			method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
			at = @At(value = "STORE"),
			ordinal = 1
	)
	private static int perfect$modifyBlockLight(int blockLight) {
		return fullBright.getLuminance(false, blockLight);
	}*/
}
