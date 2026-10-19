package com.perfect.client.inject.mixin.module.noRender;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
			method = "renderHand",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
		if (noRender.contains(Elements.HANDS)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "showFloatingItem",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onShowFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
		if (floatingItem.getItem() == Items.TOTEM_OF_UNDYING && noRender.contains(Elements.TOTEM_ANIMATION)) {
			ci.cancel();
		}
	}

	@ModifyExpressionValue(
			method = "renderWorld",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Math;max(FF)F",
					ordinal = 0
			)
	)
	private float perfect$applyCameraTransformations(float original) {
		return noRender.contains(Elements.NAUSEA) ? 0 : original;
	}
}
