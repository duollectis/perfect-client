package com.perfect.client.inject.mixin.module.legacyPotions;

import com.perfect.client.functional.function.visuals.legacyPotions.LegacyPotions;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.legacyPotions;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {

	@Inject(
			method = "getColor",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$modifyColor(CallbackInfoReturnable<Integer> cir) {
		if (legacyPotions.isEnabled()) {
			Integer oldPotionColor = LegacyPotions.legacyPotionColors.get(this);

			if (oldPotionColor != null) {
				cir.setReturnValue(oldPotionColor);
			}
		}
	}
}
