package com.perfect.client.inject.mixin.postProcessManager;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.perfect.client.core.render.postprocess.PostProcessManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(
			method = "render",
			at = @At("HEAD")
	)
	private void perfect$onRenderHead(
			ObjectAllocator allocator,
			RenderTickCounter tickCounter,
			boolean renderBlockOutline,
			Camera camera,
			Matrix4f positionMatrix,
			Matrix4f projectionMatrix,
			GpuBufferSlice fog,
			Vector4f fogColor,
			boolean shouldRenderSky,
			CallbackInfo ci
	) {
		PostProcessManager.beginWrite();
	}

	@Inject(
			method = "onResized",
			at = @At("HEAD")
	)
	private void perfect$onResized(int width, int height, CallbackInfo info) {
		PostProcessManager.onResized(width, height);
	}
}
