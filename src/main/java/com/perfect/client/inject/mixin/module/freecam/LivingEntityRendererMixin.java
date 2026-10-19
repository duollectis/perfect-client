package com.perfect.client.inject.mixin.module.freecam;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.perfect.client.manager.system.FunctionManager.freecam;

@Mixin(
		value = LivingEntityRenderer.class,
		priority = Integer.MAX_VALUE
)
public class LivingEntityRendererMixin {

	@ModifyExpressionValue(
			method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"
			)
	)
	private Entity perfect$hasLabelGetCameraEntityProxy(Entity cameraEntity) {
		return freecam.isEnabled() ? null : cameraEntity;
	}
}
