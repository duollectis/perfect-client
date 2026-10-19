package com.perfect.client.inject.mixin.module.hands;

import com.google.common.base.MoreObjects;
import com.llamalad7.mixinextras.sugar.Local;
import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.postprocess.PostProcessManager;
import com.perfect.client.core.render.postprocess.processors.EntityProcessor;
import com.perfect.client.core.render.renderer.Render;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

import static com.perfect.client.manager.system.FunctionManager.hands;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

	@Shadow
	private float equipProgressMainHand;

	@Shadow
	private float lastEquipProgressMainHand;

	@Shadow
	private float equipProgressOffHand;

	@Shadow
	private float lastEquipProgressOffHand;

	@Shadow
	private ItemStack mainHand;

	@Shadow
	private ItemStack offHand;

	@Final
	@Shadow
	private MinecraftClient client;

	@Shadow
	protected abstract void renderFirstPersonItem(
			AbstractClientPlayerEntity player,
			float tickProgress,
			float pitch,
			Hand hand,
			float swingProgress,
			ItemStack item,
			float equipProgress,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light
	);

	@Inject(
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
			at = @At("HEAD")
	)
	private void perfect$renderItemHead(
			float tickProgress,
			MatrixStack matrices,
			VertexConsumerProvider.Immediate vertexConsumers,
			ClientPlayerEntity player,
			int light,
			CallbackInfo info
	) {
		perfect$draw(
				tickProgress,
				matrices,
				vertexConsumers,
				player,
				15,
				PostProcessManager.HAND_OUTLINE_PROCESSOR,
				hands.getOutlineColor().getColor()
		);
	}

	@ModifyArgs(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
	private void setArgs(Args args) {
		if (hands.getSwingChange()) {
			args.set(6, hands.getY(args.get(3)));
			args.set(4, hands.getSwing(args.get(3)));
		}
	}

	@ModifyArgs(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 1))
	private void perfect$modifyTransformations(Args args, @Local(
			ordinal = 0,
			argsOnly = true
	) Arm arm) {
		if (hands.getChangeEatAnimation()) {
			args.set(0, hands.getOffsetX(arm));
			args.set(1, hands.getOffsetY());
			args.set(2, hands.getOffsetZ());
		}
	}

	@ModifyArgs(
			method = "applyEatOrDrinkTransformation",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0)
	)
	private void perfect$modifyEatShake(Args args) {
		if (hands.getChangeEatAnimation()) {
			float currentY = args.get(1);

			args.set(1, currentY * hands.getEatMultiplier());
		}
	}

	@Unique
	private void perfect$draw(
			float tickProgress,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			ClientPlayerEntity player,
			int light,
			EntityProcessor shader,
			Color color
	) {
		if (shader.skipDraw() || PostProcessManager.isCustom(vertexConsumers) || color == null) {
			return;
		}

		Framebuffer shaderFramebuffer = shader.getFramebuffer();

		FramebufferUtils.beginWrite(shaderFramebuffer);
		FramebufferUtils.clear(shaderFramebuffer);

		shader.getVertexConsumerProvider().setColor(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				color.getAlpha()
		);

		perfect$renderItemIternal(
				tickProgress,
				matrices,
				shader.getVertexConsumerProvider(),
				player,
				light
		);

		shader.getVertexConsumerProvider().draw();

		FramebufferUtils.beginWrite(client.getFramebuffer());

		Render.enableDepthTest();
		Render.depthMask(false);

		shader.draw();

		Render.depthMask(true);
	}

	// Это код самого майна
	@Unique
	public void perfect$renderItemIternal(
			float tickProgress,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			ClientPlayerEntity player,
			int light
	) {
		float swingProgress = player.getHandSwingProgress(tickProgress);
		Hand preferredHand = MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
		float pitch = player.getLerpedPitch(tickProgress);
		HeldItemRenderer.HandRenderType handRenderType = HeldItemRenderer.getHandRenderType(player);

		float lastRenderPitch = MathHelper.lerp(tickProgress, player.lastRenderPitch, player.renderPitch);
		float lastRenderYaw = MathHelper.lerp(tickProgress, player.lastRenderYaw, player.renderYaw);

		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(
				(player.getPitch(tickProgress) - lastRenderPitch) * 0.1f));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
				(player.getYaw(tickProgress) - lastRenderYaw) * 0.1f));

		renderHand(
				handRenderType.renderMainHand,
				Hand.MAIN_HAND,
				preferredHand,
				swingProgress,
				tickProgress,
				pitch,
				matrices,
				vertexConsumers,
				player,
				light
		);
		renderHand(
				handRenderType.renderOffHand,
				Hand.OFF_HAND,
				preferredHand,
				swingProgress,
				tickProgress,
				pitch,
				matrices,
				vertexConsumers,
				player,
				light
		);
	}

	@Unique
	private void renderHand(
			boolean shouldRender,
			Hand hand,
			Hand preferredHand,
			float swingProgress,
			float tickProgress,
			float pitch,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			ClientPlayerEntity player,
			int light
	) {
		if (!shouldRender) {
			return;
		}

		float handSwing = 0.0f;
		if (hand == preferredHand) {
			handSwing = swingProgress;
		}

		float equipProgress = 1.0f - getEquipProgress(hand, tickProgress);

		if (hands.getSwingChange()) {
			equipProgress = hands.getY(hand);
			handSwing = hands.getSwing(hand);
		}

		renderFirstPersonItem(
				player,
				tickProgress,
				pitch,
				hand,
				handSwing,
				getItemStack(hand),
				equipProgress,
				matrices,
				vertexConsumers,
				light
		);
	}

	@Unique
	private float getEquipProgress(Hand hand, float tickProgress) {
		if (hand == Hand.MAIN_HAND) {
			return MathHelper.lerp(tickProgress, this.lastEquipProgressMainHand, this.equipProgressMainHand);
		}
		else {
			return MathHelper.lerp(tickProgress, this.lastEquipProgressOffHand, this.equipProgressOffHand);
		}
	}

	@Unique
	private ItemStack getItemStack(Hand hand) {
		return hand == Hand.MAIN_HAND ? this.mainHand : this.offHand;
	}
}
