package com.perfect.client.inject.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.event.events.entity.EntityPushEvent;
import com.perfect.client.core.event.events.movement.JumpVelocityMultiplierEvent;
import com.perfect.client.core.event.events.movement.MovementForCollisionsEvent;
import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.event.events.movement.UpdateVelocityEvent;
import com.perfect.client.core.utils.mixin.MixinUtils;
import com.perfect.client.functional.function.misc.fakePlayer.utils.FakePlayerEntity;
import com.perfect.client.inject.mixinInterface.IVec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.PerfectClient.mc;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(
			method = "pushAwayFrom",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void perfect$pushAwayFrom(Entity entity, CallbackInfo ci) {
		if (entity instanceof FakePlayerEntity) {
			ci.cancel();
			return;
		}

		EntityPushEvent event = new EntityPushEvent(MixinUtils.toEntity(this), entity);

		PerfectClient.EVENT_MANAGER.onEntityPush(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "move",
			at = @At("HEAD")
	)
	private void perfect$onMove(MovementType type, Vec3d movement, CallbackInfo info) {
		if (MixinUtils.isClientPlayer(this)) {
			PlayerMoveEvent event = new PlayerMoveEvent(
					type,
					movement.x,
					movement.y,
					movement.z
			);

			PerfectClient.EVENT_MANAGER.onPrePlayerMove(event);

			((IVec3d) movement).perfect$set(event.getX(), event.getY(), event.getZ());
		}
	}

	@Inject(
			method = "move",
			at = @At("RETURN")
	)
	private void perfect$onMovePost(MovementType type, Vec3d movement, CallbackInfo info) {
		if (MixinUtils.isClientPlayer(this)) {
			PerfectClient.EVENT_MANAGER.onPostPlayerMove();
		}
	}

	@ModifyReturnValue(
			method = "getJumpVelocityMultiplier",
			at = @At("RETURN")
	)
	private float perfect$onGetJumpVelocityMultiplier(float original) {
		if (MixinUtils.isClientPlayer(this)) {
			JumpVelocityMultiplierEvent event = new JumpVelocityMultiplierEvent(original);
			PerfectClient.EVENT_MANAGER.onJumpVelocityMultiplier(event);
			return original * event.getMultiplier();
		}

		return original;
	}

	@Inject(
			method = "updateVelocity",
			at = @At("TAIL")
	)
	public void perfect$updateVelocityTailHook(float speed, Vec3d movementInput, CallbackInfo ci) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (MixinUtils.isClientPlayer(this)) {
			PerfectClient.EVENT_MANAGER.onPostUpdateVelocity();
		}
	}

	@Inject(
			method = "setRemoved",
			at = @At("HEAD")
	)
	public void perfect$setRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onEntityRemove(new EntityEvent(MixinUtils.toEntity(this)));
	}

	@Inject(
			method = "updateVelocity",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$updateVelocityHook(float speed, Vec3d movementInput, CallbackInfo ci) {
		if (mc.player != null && MixinUtils.isClientPlayer(this)) {
			UpdateVelocityEvent event = new UpdateVelocityEvent(
					movementInput,
					speed,
					mc.player.getYaw()
			);

			PerfectClient.EVENT_MANAGER.onPreUpdateVelocity(event);

			if (event.isCancelled()) {
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
		if (!MixinUtils.isClientPlayer(this)) {
			return;
		}

		MovementForCollisionsEvent event = new MovementForCollisionsEvent(movement);

		PerfectClient.EVENT_MANAGER.onMovementForCollisions(event);

		if (event.isCancelled()) {
			cir.setReturnValue(event.getMovement());
		}
	}
}
