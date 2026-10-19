package com.perfect.client.inject.mixin.module.fullBright.luminance.sodium;

import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.perfect.client.manager.system.FunctionManager.fullBright;

@Mixin(value = LightDataAccess.class, remap = false)
public class LightDataAccessMixin {

	@ModifyVariable(
			method = "compute",
			at = @At(value = "STORE"),
			name = "sl"
	)
	private int perfect$modifySkyLight(int skyLight) {
		return fullBright.getLuminance(true, skyLight);
	}

	@ModifyVariable(
			method = "compute",
			at = @At(value = "STORE"),
			name = "bl"
	)
	private int perfect$modifyBlockLight(int blockLight) {
		return fullBright.getLuminance(false, blockLight);
	}
}
