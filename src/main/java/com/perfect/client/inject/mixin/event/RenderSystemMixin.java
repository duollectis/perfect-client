package com.perfect.client.inject.mixin.event;

import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.perfect.client.PerfectClient;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

import static com.perfect.client.manager.system.FunctionManager.testFunction;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {

	@Inject(
			method = "initRenderer",
			at = @At("TAIL")
	)
	private static void perfect$initRenderer(
			long windowHandle,
			int debugVerbosity,
			boolean sync,
			BiFunction<Identifier, ShaderType, String> shaderSourceGetter,
			boolean renderDebugLabels,
			CallbackInfo ci
	) {
		PerfectClient.onInitializeGraphics();
	}

	@Inject(
			method = "limitDisplayFPS(I)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void perfect$limitDisplayFPS(
			int fps, CallbackInfo ci
	) {
		if (testFunction.needReset.get()) {
			ci.cancel();
		}
	}
}
