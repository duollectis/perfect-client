package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@ModifyVariable(
			method = "renderItem",
			at = @At("HEAD"),
			argsOnly = true
	)
	private static ItemRenderState.Glint prefect$renderItem(ItemRenderState.Glint glint) {
		if (noRender.contains(Elements.ENCHANT_GLINT)) {
			return ItemRenderState.Glint.NONE;
		}

		return glint;
	}
}