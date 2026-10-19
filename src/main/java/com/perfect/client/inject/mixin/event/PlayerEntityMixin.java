package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.movement.ClipAtEdgeEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.utils.mixin.MixinUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Shadow
	public abstract void remove(Entity.RemovalReason reason);

	@Inject(
			method = "isPushedByFluids",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void perfect$isPushedByFluids(CallbackInfoReturnable<Boolean> cir) {
		if (!MixinUtils.isClientPlayer(this)) {
			return;
		}
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onFluidPush(event);

		if (event.isCancelled()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
			method = "clipAtLedge",
			at = @At("HEAD"),
			cancellable = true
	)
	protected void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
		if (!MixinUtils.isClientPlayer(this)) {
			return;
		}

		ClipAtEdgeEvent event = new ClipAtEdgeEvent();
		PerfectClient.EVENT_MANAGER.onClipAtEdge(event);

		if (event.isCancelled()) {
			info.setReturnValue(event.isClip());
		}
	}
}
