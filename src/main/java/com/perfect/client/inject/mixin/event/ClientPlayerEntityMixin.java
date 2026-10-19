package com.perfect.client.inject.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.movement.MovementIterationEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.manager.system.FunctionManager.hands;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

	@Shadow
	@Final
	protected MinecraftClient client;

	@Shadow
	public abstract void tickMovement();

	@Shadow
	protected abstract void sendMovementPackets();

	@Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At(value = "HEAD"))
	private void swingHand(Hand hand, CallbackInfo ci) {
		hands.startSwing(hand);
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V",
					ordinal = 0,
					shift = At.Shift.AFTER
			),
			cancellable = true
	)
	private void perfect$postUpdateHook(CallbackInfo info) {
		if (PlayerUtils.isTickUpdateLock()) {
			return;
		}

		MovementIterationEvent event = new MovementIterationEvent();
		PerfectClient.EVENT_MANAGER.onMovementIteration(event);

		if (event.isCancelled()) {
			info.cancel();

			if (event.getIterations() > 0) {
				PlayerUtils.setTickUpdateLock(true);

				for (int i = 0; i < event.getIterations(); i++) {
					tickMovement();
					sendMovementPackets();
				}

				PlayerUtils.setTickUpdateLock(false);
			}
		}
	}

	@Inject(
			method = "tick",
			at = @At("HEAD")
	)
	public void perfect$tickHook(CallbackInfo info) {
		if (client.player != null && client.world != null) {
			PerfectClient.EVENT_MANAGER.onClientPlayerUpdate();
		}
	}

	@Inject(
			method = "sendMovementPackets",
			at = @At("TAIL")
	)
	private void perfect$onSendMovementPacketsTail(CallbackInfo info) {
		PerfectClient.EVENT_MANAGER.onPostSendMovement();
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
					ordinal = 1,
					shift = At.Shift.AFTER
			)
	)
	private void perfect$onTickHasVehicleAfterSendPackets(CallbackInfo info) {
		PerfectClient.EVENT_MANAGER.onPostSendMovement();
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
					ordinal = 1
			)
	)
	private void perfect$onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
		if (!clientConfig.getMovementSync()) {
			perfect$forceRotate();
		}

		PerfectClient.EVENT_MANAGER.onPreSendMovement();
	}


	@Inject(
			method = "sendMovementPackets",
			at = @At("HEAD")
	)
	private void perfect$onSendMovementPacketsHead(CallbackInfo info) {
		if (!clientConfig.getMovementSync()) {
			perfect$forceRotate();
		}

		PerfectClient.EVENT_MANAGER.onPreSendMovement();
	}

	@Inject(
			method = "pushOutOfBlocks",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void perfect$pushOutOfBlocks(double x, double z, CallbackInfo ci) {
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onBlockPush(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@ModifyExpressionValue(
			method = "applyMovementSpeedFactors",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
			)
	)
	private boolean perfect$isUsingItem(boolean defaultValue) {
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onItemSlowDown(event);

		if (event.isCancelled()) {
			return false;
		}
		return defaultValue;
	}

	@ModifyExpressionValue(
			method = "shouldSlowDown",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isInSneakingPose()Z"
			)
	)
	private boolean perfect$isInSneakingPose(boolean defaultValue) {
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onSneakSlowDown(event);

		if (event.isCancelled()) {
			return false;
		}
		return defaultValue;
	}

	@ModifyExpressionValue(
			method = "shouldSlowDown",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCrawling()Z"
			)
	)
	private boolean perfect$isCrawling(boolean defaultValue) {
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onCrawlSlowDown(event);

		if (event.isCancelled()) {
			return false;
		}
		return defaultValue;
	}

	@Inject(
			method = "tickMovement",
			at = @At("HEAD")
	)
	public void perfect$tickMovement(CallbackInfo ci) {
		if (clientConfig.getMovementSync()) {
			perfect$forceRotate();
		}
	}

	@Unique
	private static void perfect$forceRotate() {
		PerfectClient.EVENT_MANAGER.onRotate();
	}
}
