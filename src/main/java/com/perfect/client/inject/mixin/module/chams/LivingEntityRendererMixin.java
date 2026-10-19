package com.perfect.client.inject.mixin.module.chams;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.perfect.client.core.render.renderer.ModelRenderer;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.inject.mixinInterface.IEntityRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.manager.system.FunctionManager.chams;

@Mixin(value = LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

	@Inject(
			method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"),
			cancellable = true
	)
	private void perfect$cancelFeatures(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (ModelRenderer.isRenderingOutline) {
			ci.cancel();
		}
	}

	@WrapWithCondition(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
	private boolean perfect$render(M instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, S state, MatrixStack matrices, VertexConsumerProvider consumers, int i) {
		Entity entity = ((IEntityRenderState) state).perfect$getEntity();

		if (entity == mc.player && chams.getIgnoreSelf()) {
			return true;
		}

		if (chams.getPlayers() && chams.getPlayersChangeColor() && entity instanceof PlayerEntity) {
			instance.render(matrixStack, vertexConsumer, light, overlay, ColorUtils.getRgba(chams.getPlayerColor()));

			return false;
		}

		return true;
	}
}
