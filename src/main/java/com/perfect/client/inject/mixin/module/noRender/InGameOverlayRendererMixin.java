package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

	@Inject(
			method = "renderFireOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void perfect$onRenderFireOverlay(
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.FIRE_OVERLAY)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderInWallOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void perfect$render(
			Sprite sprite,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.WALL_OVERLAY)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderUnderwaterOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void perfect$onRenderUnderwaterOverlay(
			MinecraftClient client,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.LIQUID_OVERLAY)) {
			ci.cancel();
		}
	}
}
