package com.perfect.client.functional.function.visuals.auctionHelper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;

@Getter
@AllArgsConstructor
public enum SwordEnchantments {
	SHARPNESS(Enchantments.SHARPNESS),
	UNBREAKING(Enchantments.UNBREAKING),
	KNOCKBACK(Enchantments.KNOCKBACK),
	FIRE_ASPECT(Enchantments.FIRE_ASPECT),
	SWEEPING_EDGE(Enchantments.SWEEPING_EDGE),
	LOOTING(Enchantments.LOOTING),
	MENDING(Enchantments.MENDING);

	private final RegistryKey<Enchantment> enchantment;
}

	