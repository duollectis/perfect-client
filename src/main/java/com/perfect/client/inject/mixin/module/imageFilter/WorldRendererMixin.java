package com.perfect.client.inject.mixin.module.imageFilter;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.perfect.client.functional.function.visuals.imageFilter.utils.CustomLightMap;
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

import static com.perfect.client.manager.system.FunctionManager.imageFilter;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(
			method = "render",
			at = @At("HEAD")
	)
	public void perfect$onRenderHead(
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
		if (imageFilter.isEnabled()) {
			CustomLightMap.setRendering(true);
		}
	}

	@Inject(
			method = "render",
			at = @At("TAIL")
	)
	public void perfect$onRenderTail(
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
		CustomLightMap.setRendering(false);
	}
}
