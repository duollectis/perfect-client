package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.HeldItemRendererEvent;
import com.perfect.client.core.render.postprocess.PostProcessManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	@Inject(
			method = "renderFirstPersonItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			),
			cancellable = true)
	private void perfect$onRenderItem(
			AbstractClientPlayerEntity player,
			float tickDelta,
			float pitch,
			Hand hand,
			float swingProgress,
			ItemStack item,
			float equipProgress,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light,
			CallbackInfo ci
	) {
		if (!PostProcessManager.HAND_OUTLINE_PROCESSOR.skipDraw() && !PostProcessManager.isCustom(vertexConsumers)) {
			matrices.pop();
			ci.cancel();
		}

		PerfectClient.EVENT_MANAGER.onHeldItemRenderer(new HeldItemRendererEvent(hand, matrices));
	}

	// Новый инжект специально для пустой руки
	@Inject(
			method = "renderFirstPersonItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"
			),
			cancellable = true)
	private void perfect$onRenderArm(
			AbstractClientPlayerEntity player,
			float tickDelta,
			float pitch,
			Hand hand,
			float swingProgress,
			ItemStack item,
			float equipProgress,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light,
			CallbackInfo ci
	) {
		if (!PostProcessManager.HAND_OUTLINE_PROCESSOR.skipDraw() && !PostProcessManager.isCustom(vertexConsumers)) {
			matrices.pop();
			ci.cancel();
		}

		//PerfectClient.EVENT_MANAGER.onHeldItemRenderer(new HeldItemRendererEvent(hand, matrices));
	}
}