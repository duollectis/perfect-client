package com.perfect.client.inject.mixin.module.noRender;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(StatusEffectFogModifier.class)
public abstract class StatusEffectFogModifierMixin {

	@Shadow
	public abstract RegistryEntry<StatusEffect> getStatusEffect();

	@ModifyReturnValue(
			method = "shouldApply",
			at = @At("RETURN")
	)
	private boolean perfect$onShouldApply(boolean original) {
		if (shouldCancel(StatusEffects.BLINDNESS, Elements.BLINDNESS)
				|| shouldCancel(StatusEffects.DARKNESS, Elements.DARKNESS)
		) {
			return false;
		}

		return original;
	}

	@Unique
	private boolean shouldCancel(RegistryEntry<StatusEffect> statusEffect, Elements elements) {
		return getStatusEffect() == statusEffect && noRender.contains(elements);
	}
}