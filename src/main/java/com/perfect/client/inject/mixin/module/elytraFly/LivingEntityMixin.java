package com.perfect.client.inject.mixin.module.elytraFly;

import com.perfect.client.functional.function.movement.elytraFly.ElytraFly;
import com.perfect.client.functional.function.movement.elytraFly.modes.BounceMode;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.elytraFly;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Unique
	private boolean previousElytra = false;

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "isGliding", at = @At("TAIL"), cancellable = true)
	public void recastOnLand(CallbackInfoReturnable<Boolean> cir) {
		if (!((Object) this instanceof ClientPlayerEntity)) {
			return;
		}

		boolean elytra = cir.getReturnValue();

		if (previousElytra && !elytra && elytraFly.isEnabled() && elytraFly.isActive(ElytraFly.Mode.BOUNCE)) {
			cir.setReturnValue(BounceMode.recastElytra());
		}

		previousElytra = elytra;
	}
}
