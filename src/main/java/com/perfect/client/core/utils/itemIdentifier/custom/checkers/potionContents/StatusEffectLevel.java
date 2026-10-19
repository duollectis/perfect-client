package com.perfect.client.core.utils.itemIdentifier.custom.checkers.potionContents;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public record StatusEffectLevel(
		RegistryEntry<StatusEffect> statusEffect,
		int duration,
		int level
) {
}
