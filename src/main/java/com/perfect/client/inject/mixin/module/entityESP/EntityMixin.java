package com.perfect.client.inject.mixin.module.entityESP;

import com.perfect.client.core.render.postprocess.PostProcessManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

import static com.perfect.client.manager.system.FunctionManager.entityESP;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(
			method = "getTeamColorValue",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onGetTeamColorValue(CallbackInfoReturnable<Integer> info) {
		if (PostProcessManager.rendering) {
			Color color = entityESP.getOutlineColor().getColor();

			if (color != null) {
				info.setReturnValue(color.getRGB());
			}
		}
	}
}
