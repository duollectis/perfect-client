package com.perfect.client.inject.mixin.module.ambience;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.perfect.client.core.utils.color.Color4f;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.ambience;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

	@ModifyExpressionValue(
			method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/fog/FogRenderer;getFogColor(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IFZ)Lorg/joml/Vector4f;"
			)
	)
	private Vector4f perfect$modifyFogColor(
			Vector4f original,
			Camera camera,
			int viewDistance,
			boolean thick,
			RenderTickCounter tickCounter,
			float skyDarkness,
			ClientWorld world
	) {
		if (ambience.isEnabled() && ambience.getCustomFog() && ambience.getCustomFogColor()) {
			return new Color4f(ambience.fogColor()).translateToVec4f();
		}
		else {
			return original;
		}
	}

	@Inject(
			method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/fog/FogData;renderDistanceEnd:F",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			)
	)
	private void perfect$modifyFogRange(
			Camera camera,
			int viewDistance,
			boolean thick,
			RenderTickCounter tickCounter,
			float skyDarkness,
			ClientWorld world,
			CallbackInfoReturnable<Vector4f> cir,
			@Local FogData fogData
	) {
		if (ambience.isEnabled() && ambience.getCustomFog() && ambience.getCustomFogRange()) {
			fogData.renderDistanceStart = ambience.getFogStart();
			fogData.renderDistanceEnd = ambience.getFogEnd();

			fogData.environmentalStart = ambience.getFogStart();
			fogData.environmentalEnd = ambience.getFogEnd();
		}
	}
}