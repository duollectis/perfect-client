package com.perfect.client.inject.mixin.module.blockHighlight;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.blockSelection;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(
			method = "drawBlockOutline",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$drawBlockOutline(
			MatrixStack matrices,
			VertexConsumer vertexConsumer,
			Entity entity,
			double cameraX,
			double cameraY,
			double cameraZ,
			BlockPos pos,
			BlockState state,
			int color,
			CallbackInfo ci
	) {
		if (blockSelection.isEnabled()) {
			ci.cancel();
		}
	}
}
