package com.perfect.client.core.utils.itemIdentifier.custom.checkers.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;

public record EnchantmentLevel(
		RegistryKey<Enchantment> enchantment,
		int level
) {}