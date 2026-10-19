package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.movement.TravelEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.utils.mixin.MixinUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(
			method = "travel",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$travelHook(Vec3d movementInput, CallbackInfo ci) {
		if (mc.player != null && MixinUtils.isClientPlayer(this)) {
			TravelEvent event = new TravelEvent(movementInput);
			PerfectClient.EVENT_MANAGER.onPreTravel(event);

			if (event.isCancelled()) {
				mc.player.move(MovementType.SELF, event.getMovement());
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "travel",
			at = @At("RETURN"),
			cancellable = true
	)
	public void perfect$travelPostHook(Vec3d movementInput, CallbackInfo ci) {
		if (mc.player != null && MixinUtils.isClientPlayer(this)) {
			TravelEvent event = new TravelEvent(movementInput);
			PerfectClient.EVENT_MANAGER.onPostTravel(event);

			if (event.isCancelled()) {
				mc.player.move(MovementType.SELF, event.getMovement());
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "jump",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$jump(CallbackInfo ci) {
		if (!MixinUtils.isClientPlayer(this)) {
			return;
		}

		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onPreJump(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "jump",
			at = @At("RETURN")
	)
	public void perfect$jumpReturn(CallbackInfo ci) {
		if (MixinUtils.isClientPlayer(this)) {
			PerfectClient.EVENT_MANAGER.onPostJump();
		}
	}
}
