package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.block.BlockEvent;
import com.perfect.client.core.event.events.block.InteractBlockEvent;
import com.perfect.client.core.event.events.entity.AttackEntityEvent;
import com.perfect.client.core.event.system.Cancellable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Inject(
			method = "isBreakingBlock",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	public void perfect$isBreakingBlock(CallbackInfoReturnable<Boolean> cir) {
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onMultiTask(event);

		if (event.isCancelled()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
			method = "attackBlock",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$attackBlock(
			BlockPos pos,
			Direction direction,
			CallbackInfoReturnable<Boolean> cir
	) {
		BlockEvent event = new BlockEvent(pos, direction);
		PerfectClient.EVENT_MANAGER.onAttackBlock(event);

		if (event.isCancelled()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
			method = "attackBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
					ordinal = 1
			)
	)
	public void perfect$startBreakingBlock(
			BlockPos pos,
			Direction direction,
			CallbackInfoReturnable<Boolean> cir
	) {
		BlockEvent event = new BlockEvent(pos, direction);
		PerfectClient.EVENT_MANAGER.onStartBreakingBlock(event);
	}

	@Inject(
			method = "updateBlockBreakingProgress",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$updateBlockBreakingProgress(
			BlockPos pos,
			Direction direction,
			CallbackInfoReturnable<Boolean> cir
	) {
		BlockEvent event = new BlockEvent(pos, direction);
		PerfectClient.EVENT_MANAGER.onBlockBreakingProgress(event);

		if (event.isCancelled()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
			method = "attackEntity",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onAttackEntity(PlayerEntity player, Entity target, CallbackInfo info) {
		AttackEntityEvent event = new AttackEntityEvent(target);
		PerfectClient.EVENT_MANAGER.onAttackEntity(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(
			method = "clickSlot",
			at = @At(value = "RETURN")
	)
	public void perfect$clickSlot(
			int syncId,
			int slotId,
			int button,
			SlotActionType actionType,
			PlayerEntity player,
			CallbackInfo ci
	) {
		PerfectClient.EVENT_MANAGER.onClickSlot();
	}

	@Inject(
			method = "interactBlock",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$interactBlockPre(
			ClientPlayerEntity player,
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir
	) {
		InteractBlockEvent event = new InteractBlockEvent(hand, hitResult);
		PerfectClient.EVENT_MANAGER.onInteractBlock(event);

		if (event.isCancelled()) {
			cir.setReturnValue(ActionResult.FAIL);
		}
	}

	@Inject(
			method = "stopUsingItem",
			at = @At("HEAD")
	)
	public void perfect$stopUsingItemHead(PlayerEntity player, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onPreUsingItem();
	}

	@Inject(
			method = "stopUsingItem",
			at = @At("TAIL")
	)
	public void perfect$stopUsingItemTail(PlayerEntity player, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onPostUsingItem();
	}
}
