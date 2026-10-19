package com.perfect.client.inject.mixin.module.fullBright.luminance;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.perfect.client.manager.system.FunctionManager.fullBright;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@ModifyReturnValue(
			method = "getSkyLight",
			at = @At("RETURN")
	)
	private int perfect$modifySkyLight(int skyLight) {
		return fullBright.getLuminance(true, skyLight);
	}

	@ModifyReturnValue(
			method = "getBlockLight",
			at = @At("RETURN")
	)
	private int perfect$modifyBlockLight(int blockLight) {
		return fullBright.getLuminance(false, blockLight);
	}
}
