package com.perfect.client.inject.mixin.module.ambience;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.ambience;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(
			method = "renderSky",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$renderSky(
			FrameGraphBuilder frameGraphBuilder,
			Camera camera,
			float tickProgress,
			GpuBufferSlice fog,
			CallbackInfo ci
	) {
		if (ambience.isEnabled() && ((ambience.getCustomFog() && ambience.getThickFog()) || ambience.getFlatSky())) {
			ci.cancel();
		}
	}
}
