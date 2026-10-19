package com.perfect.client.inject.mixin.module.noRender;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.perfect.client.functional.function.visuals.noRender.Elements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(targets = "net.minecraft.client.font.TextRenderer$Drawer")
public class TextRendererMixin {

	@ModifyExpressionValue(
			method = "accept",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/text/Style;isObfuscated()Z"
			)
	)
	private boolean perfect$onRenderObfuscatedStyle(boolean original) {
		return !noRender.contains(Elements.TEXT_OBFUSCATION) && original;
	}
}
