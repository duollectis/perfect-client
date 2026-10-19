package com.perfect.client.inject.mixin.module.freecam;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.perfect.client.manager.system.FunctionManager.freecam;

@Mixin(Camera.class)
public class CameraMixin {

	@Shadow
	private boolean thirdPerson;

	@Inject(
			method = "update",
			at = @At("TAIL")
	)
	private void perfect$updateHook(
			BlockView area,
			Entity focusedEntity,
			boolean thirdPerson,
			boolean inverseView,
			float tickDelta,
			CallbackInfo ci
	) {
		if (freecam.isEnabled()) {
			this.thirdPerson = true;
		}
	}

	@ModifyArgs(
			method = "update",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"
			)
	)
	private void perfect$setRotationHook(Args args) {
		if (freecam.isEnabled()) {
			args.setAll(freecam.getYaw(), freecam.getPitch());
		}
	}

	@ModifyArgs(
			method = "update",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"
			)
	)
	private void perfect$setPosHook(Args args) {
		if (freecam.isEnabled()) {
			args.setAll(freecam.getX(), freecam.getY(), freecam.getZ());
		}
	}
}