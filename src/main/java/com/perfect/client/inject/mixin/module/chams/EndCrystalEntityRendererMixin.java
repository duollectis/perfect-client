package com.perfect.client.inject.mixin.module.chams;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.chams;

@Mixin(EndCrystalEntityRenderer.class)
public class EndCrystalEntityRendererMixin {

	@Shadow
	@Final
	@Mutable
	private static RenderLayer END_CRYSTAL;

	@Shadow
	@Final
	private static Identifier TEXTURE;

	@Shadow
	@Final
	private EndCrystalEntityModel model;

	@Inject(
			method = "render(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At("HEAD")
	)
	private void perfect$changeCrystalsTexture(
			EndCrystalEntityRenderState endCrystalEntityRenderState,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			CallbackInfo ci
	) {
		END_CRYSTAL = RenderLayer.getEntityTranslucent(TEXTURE);
	}

	@Inject(
			method = "render(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"
			)
	)
	private void perfect$changeCrystalsScale(
			EndCrystalEntityRenderState endCrystalEntityRenderState,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			CallbackInfo info
	) {
		if (!chams.getCrystals()) {
			return;
		}

		float v = (float) chams.getCrystalSize();
		matrixStack.scale(v, v, v);
	}

	@WrapWithCondition(
			method = "render(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/entity/model/EndCrystalEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
			)
	)
	private boolean perfect$changeCrystalsColor(
			EndCrystalEntityModel instance,
			MatrixStack matrices,
			VertexConsumer vertices,
			int light,
			int overlay
	) {
		if (chams.getCrystals() && chams.getCrystalsChangeColor()) {
			model.render(matrices, vertices, light, overlay, ColorUtils.getRgba(chams.getCrystalsColor()));

			return false;
		}

		return true;
	}
}
