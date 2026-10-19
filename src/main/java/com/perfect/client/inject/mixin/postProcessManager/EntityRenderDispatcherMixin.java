package com.perfect.client.inject.mixin.postProcessManager;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.perfect.client.core.render.postprocess.PostProcessManager;
import com.perfect.client.inject.mixinInterface.IEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	@Inject(
			method = "renderShadow",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void perfect$onRenderShadow(
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			EntityRenderState renderState,
			float opacity,
			WorldView world,
			float radius,
			CallbackInfo ci
	) {
		if (PostProcessManager.rendering) {
			ci.cancel();
		}
	}

	@ModifyExpressionValue(
			method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/entity/EntityRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;"
			)
	)
	private <E extends Entity, S extends EntityRenderState> S render$getAndUpdateRenderState(S state, E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer) {
		((IEntityRenderState) state).perfect$setEntity(entity);

		return state;
	}
}
