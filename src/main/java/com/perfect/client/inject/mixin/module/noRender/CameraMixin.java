package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(Camera.class)
public class CameraMixin {

	@Inject(
			method = "getSubmersionType",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$getSubmergedFluidState(CallbackInfoReturnable<CameraSubmersionType> ci) {
		if (noRender.contains(Elements.LIQUID_OVERLAY)) {
			ci.setReturnValue(CameraSubmersionType.NONE);
		}
	}
}