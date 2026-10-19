package com.perfect.client.inject.mixin.module.legacySneak;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.legacySneak;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Unique
	private static final float SNEAKING_EYE_HEIGHT_LEGACY = 1.54F;

	@Inject(
			method = "getBaseDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;",
			at = @At("TAIL"),
			cancellable = true
	)
	private void perfect$getBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		if (legacySneak.isEnabled() && pose == EntityPose.CROUCHING) {
			EntityDimensions resultDimensions = cir.getReturnValue();

			float height = resultDimensions.height();

			EntityDimensions modifiedDimensions = new EntityDimensions(
					resultDimensions.width(), height, SNEAKING_EYE_HEIGHT_LEGACY,
					resultDimensions.attachments(), resultDimensions.fixed()
			);

			cir.setReturnValue(modifiedDimensions);
		}
	}
}
